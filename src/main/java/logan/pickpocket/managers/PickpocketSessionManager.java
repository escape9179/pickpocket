package logan.pickpocket.managers;

import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.hooks.WorldGuardHook;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.tasks.PickpocketTask;
import logan.pickpocket.user.PickpocketUser;
import logan.pickpocket.user.RummageInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Orchestrates pickpocket attempts, rummage UI, and session cleanup (move / close inventory).
 */
public final class PickpocketSessionManager {

    private PickpocketSessionManager() {
    }

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

        thief.setVictim(victim);
        victim.setPredator(thief);

        int speedLevel = thief.getSpeedSkill().getLevel();
        float delaySeconds = thief.getSpeedSkill().getDelaySeconds(speedLevel);
        int delayTicks = (int) (delaySeconds * 20);

        Location startLocation = player.getLocation();
        Player victimPlayer = victim.getBukkitPlayer();
        if (victimPlayer == null) {
            unlinkSession(thief);
            return;
        }
        Location victimStartLocation = victimPlayer.getLocation();

        if (delayTicks <= 0) {
            completePickpocketAndOpenRummage(thief, victim);
            return;
        }

        player.sendMessage(MessageConfig.getPickpocketAttemptMessage());

        new PickpocketTask(thief, victim, delayTicks, startLocation, victimStartLocation)
                .runTaskTimer(PickpocketPlugin.getInstance(), 0L, 1L);
    }

    public static void completePickpocketAndOpenRummage(PickpocketUser thief, PickpocketUser victim) {
        RummageInventory inventory = new RummageInventory(victim);
        thief.setOpenRummageInventory(inventory);
        inventory.show(thief);
        thief.setRummaging(true);
        thief.getSpeedSkill().addExp(10);
    }

    /**
     * Clears the active pickpocket link when the attempt is cancelled (movement, disconnect, etc.).
     */
    public static void unlinkSession(PickpocketUser thief) {
        PickpocketUser v = thief.getVictim();
        if (v != null) {
            v.setPredator(null);
        }
        thief.setVictim(null);
    }

    public static void onInventoryClosed(PickpocketUser user) {
        if (user.isPlayingMinigame()) {
            user.getCurrentMinigame().stop();
            Player player = user.getBukkitPlayer();
            if (player != null) {
                player.sendMessage(MessageConfig.getPickpocketUnsuccessfulMessage());
            }
        }
        if (user.isRummaging()) {
            user.setRummaging(false);
            PickpocketUser victim = user.getVictim();
            if (victim != null) {
                victim.setPredator(null);
            }
            user.setVictim(null);
        }
    }

    public static void onPredatorMoved(Player player, PickpocketUser predator) {
        if (!predator.isPredator()) {
            return;
        }
        PickpocketUser victimProfile = predator.getVictim();
        if (victimProfile == null) {
            return;
        }
        if (predator.isPlayingMinigame()) {
            predator.getCurrentMinigame().stop();
        }
        if (predator.isRummaging()) {
            Player thiefPlayer = predator.getBukkitPlayer();
            if (thiefPlayer != null) {
                thiefPlayer.closeInventory();
            }
            predator.setRummaging(false);
        }
        player.sendMessage(MessageConfig.getPickpocketOnMoveWarningMessage());
        predator.setVictim(null);
        victimProfile.setPredator(null);
    }

    public static void onVictimMoved(PickpocketUser victim) {
        if (!victim.isVictim()) {
            return;
        }
        PickpocketUser predatorProfile = victim.getPredator();
        if (predatorProfile == null) {
            return;
        }
        if (predatorProfile.isPlayingMinigame()) {
            predatorProfile.getCurrentMinigame().stop();
        }
        if (predatorProfile.isRummaging()) {
            Player thiefPlayer = predatorProfile.getBukkitPlayer();
            if (thiefPlayer != null) {
                thiefPlayer.closeInventory();
            }
            predatorProfile.setRummaging(false);
        }
        victim.setPredator(null);
        predatorProfile.setVictim(null);
    }
}
