package logan.pickpocket.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.hooks.WorldGuardHook;
import logan.pickpocket.history.PickpocketHistoryLog;
import logan.pickpocket.inventory.RummageInventory;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.tasks.PickpocketTask;
import logan.pickpocket.user.PickpocketUser;

/**
 * Orchestrates pickpocket attempts, rummage UI, and session cleanup (move / close inventory).
 */
public final class PickpocketSessionManager {

    private static final Map<UUID, PickpocketSession> sessionByThiefId = new ConcurrentHashMap<>();
    private static final Map<UUID, PickpocketSession> sessionByVictimId = new ConcurrentHashMap<>();
    private static final Set<PickpocketUser> usersInSession = ConcurrentHashMap.newKeySet();
    private static final List<PickpocketSessionHistoryEntry> sessionHistory = Collections
            .synchronizedList(new ArrayList<>());

    private PickpocketSessionManager() {
    }

    /**
     * Returns the active session where this user is the thief or victim, or null.
     */
    public static PickpocketSession getSession(PickpocketUser user) {
        if (user == null) {
            return null;
        }
        UUID id = user.getUuid();
        PickpocketSession asThief = sessionByThiefId.get(id);
        if (asThief != null) {
            return asThief;
        }
        return sessionByVictimId.get(id);
    }

    /**
     * Immutable copy of completed sessions, newest appended on each {@link #endSession}.
     */
    public static List<PickpocketSessionHistoryEntry> getSessionHistory() {
        synchronized (sessionHistory) {
            return Collections.unmodifiableList(new ArrayList<>(sessionHistory));
        }
    }

    /**
     * Starts a pickpocket attempt, enforcing region and cooldown checks first.
     *
     * @param thief stealing user
     * @param victim target user
     */
    public static void startPickpocket(PickpocketUser thief, PickpocketUser victim) {
        Player player = thief.getBukkitPlayer();
        if (player == null) {
            return;
        }

        if (!WorldGuardHook.isPickpocketingAllowedAtPlayerRegion(player)) {
            player.sendMessage(MessageConfig.getPickpocketRegionDisallowMessage());
            return;
        }

        if (CooldownManager.hasCooldown(player)) {
            player.sendMessage(
                    MessageConfig.getCooldownNoticeMessage(
                            String.valueOf(CooldownManager.getCooldowns().get(player))));
            return;
        }

        registerSession(thief, victim);

        int speedLevel = thief.getSpeedSkill().getLevel();
        float delaySeconds = thief.getSpeedSkill().getDelaySeconds(speedLevel);
        int delayTicks = (int) (delaySeconds * 20);

        Location startLocation = player.getLocation();
        Player victimPlayer = victim.getBukkitPlayer();
        if (victimPlayer == null) {
            endSession(sessionByThiefId.get(thief.getUuid()), SessionEndReason.UNLINKED);
            return;
        }
        Location victimStartLocation = victimPlayer.getLocation();

        if (delayTicks <= 0) {
            openRummageInventory(thief, victim);
            return;
        }

        thief.playPickpocketAttemptSound(delaySeconds);

        new PickpocketTask(thief, victim, delayTicks, startLocation, victimStartLocation)
                .runTaskTimer(PickpocketPlugin.getInstance(), 0L, 1L);
    }

    /**
     * Opens rummage inventory for an active thief-victim session.
     *
     * @param thief stealing user
     * @param victim target user
     */
    public static void openRummageInventory(PickpocketUser thief, PickpocketUser victim) {
        PickpocketSession session = sessionByThiefId.get(thief.getUuid());
        if (session == null || !session.getVictim().getUuid().equals(victim.getUuid())) {
            return;
        }
        session.getRummageState().initializeCandidateSlots(victim);
        RummageInventory inventory = new RummageInventory(session);
        session.setRummageInventory(inventory);
        inventory.show();
        session.setRummaging(true);
        session.setRummageStartEpochMilli(System.currentTimeMillis());
        thief.getSpeedSkill().addExp(10);
        thief.persistSkillStats();
        thief.save();
    }

    /**
     * Clears the active pickpocket link when the attempt is cancelled (movement, disconnect, etc.).
     */
    public static void unlinkSession(PickpocketUser thief) {
        endSession(sessionByThiefId.get(thief.getUuid()), SessionEndReason.UNLINKED);
    }

    /**
     * Same as {@link #unlinkSession(PickpocketUser)} but records a specific reason (e.g. task cancel).
     */
    public static void unlinkSession(PickpocketUser thief, SessionEndReason reason) {
        endSession(sessionByThiefId.get(thief.getUuid()), reason);
    }

    /**
     * Handles inventory close events related to rummage UI lifecycle.
     *
     * @param user closing user
     */
    public static void onInventoryClosed(PickpocketUser user) {
        PickpocketSession session = getSession(user);
        if (session == null) {
            return;
        }
        if (session.isRummaging() && session.isThief(user)) {
            if (session.getRummageState().consumeSuppressNextInventoryClose()) {
                return;
            }
            session.setRummaging(false);
            endSession(session, SessionEndReason.RUMMAGE_INVENTORY_CLOSED);
        }
    }

    /**
     * Marks a rummaging thief as caught when the victim opens an inventory.
     *
     * @param user inventory-opening user
     */
    public static void onInventoryOpened(PickpocketUser user) {
        PickpocketSession session = getSession(user);
        if (session == null || !session.isVictim(user) || !session.isRummaging()) {
            return;
        }

        Player thiefPlayer = session.getThief().getBukkitPlayer();
        Player victimPlayer = session.getVictim().getBukkitPlayer();
        Location soundLocation = thiefPlayer != null
                ? thiefPlayer.getLocation()
                : (victimPlayer != null ? victimPlayer.getLocation() : null);

        if (soundLocation != null) {
            if (thiefPlayer != null) {
                thiefPlayer.playSound(soundLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            }
            if (victimPlayer != null) {
                victimPlayer.playSound(soundLocation, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            }
        }

        session.setRummaging(false);
        if (thiefPlayer != null) {
            thiefPlayer.closeInventory();
        }
        endSession(session, SessionEndReason.VICTIM_INVENTORY_OPENED);
    }

    /**
     * Ends a session when the thief moves during a pending or active attempt.
     *
     * @param player thief Bukkit player
     * @param predator thief user
     */
    public static void onPredatorMoved(Player player, PickpocketUser predator) {
        PickpocketSession session = getSession(predator);
        if (session == null || !session.isThief(predator)) {
            return;
        }
        if (session.isRummaging()) {
            Player thiefPlayer = predator.getBukkitPlayer();
            if (thiefPlayer != null) {
                thiefPlayer.closeInventory();
            }
            session.setRummaging(false);
        }
        player.sendMessage(MessageConfig.getPickpocketOnMoveWarningMessage());
        endSession(session, SessionEndReason.PREDATOR_MOVED);
    }

    /**
     * Ends a session when the victim moves during a pending or active attempt.
     *
     * @param victim victim user
     */
    public static void onVictimMoved(PickpocketUser victim) {
        PickpocketSession session = getSession(victim);
        if (session == null || !session.isVictim(victim)) {
            return;
        }
        PickpocketUser thief = session.getThief();
        if (session.isRummaging()) {
            Player thiefPlayer = thief.getBukkitPlayer();
            if (thiefPlayer != null) {
                thiefPlayer.closeInventory();
            }
            session.setRummaging(false);
        }
        endSession(session, SessionEndReason.VICTIM_MOVED);
    }

    private static void registerSession(PickpocketUser thief, PickpocketUser victim) {
        PickpocketSession session = new PickpocketSession(thief, victim);
        sessionByThiefId.put(thief.getUuid(), session);
        sessionByVictimId.put(victim.getUuid(), session);
        usersInSession.add(thief);
        usersInSession.add(victim);
    }

    private static void endSession(PickpocketSession session, SessionEndReason reason) {
        if (session == null) {
            return;
        }
        persistRummageDuration(session);
        if (!sessionByThiefId.remove(session.getThief().getUuid(), session)) {
            return;
        }
        sessionByVictimId.remove(session.getVictim().getUuid(), session);
        usersInSession.remove(session.getThief());
        usersInSession.remove(session.getVictim());
        appendHistory(session, reason);
        session.clearEphemeralState();
    }

    private static void persistRummageDuration(PickpocketSession session) {
        long elapsedMillis = session.consumeRummageElapsedMillis(System.currentTimeMillis());
        if (elapsedMillis <= 0L) {
            return;
        }
        PickpocketUser thief = session.getThief();
        thief.addRummageMillis(elapsedMillis);
        thief.save();
    }

    private static void appendHistory(PickpocketSession session, SessionEndReason reason) {
        PickpocketSessionHistoryEntry entry = new PickpocketSessionHistoryEntry(
                session.getThief().getUuid(),
                session.getVictim().getUuid(),
                System.currentTimeMillis(),
                reason);
        sessionHistory.add(entry);
        PickpocketHistoryLog.append(entry);
    }

    /**
     * Checks if a user is in a pickpocket session.
     *
     * @param user The user to check.
     * @return True if the user is in a pickpocket session, false otherwise.
     */
    public static boolean hasUserInSession(PickpocketUser user) {
        return getSession(user) != null;
    }

    /**
     * Gets the users in a pickpocket session.
     *
     * @return The users in a pickpocket session.
     */
    public static Set<PickpocketUser> getUsersInSession() {
        return Collections.unmodifiableSet(new HashSet<>(usersInSession));
    }
}
