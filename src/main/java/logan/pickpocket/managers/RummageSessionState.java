package logan.pickpocket.managers;

import logan.pickpocket.main.PickpocketUtils;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mutable per-session rummage state (rows, revealed slots, and victim slot candidates).
 */
public final class RummageSessionState {

    private static final int START_ROWS = 1;
    private static final int MIN_MAIN_INVENTORY_SLOT = 9;
    private static final int MAX_MAIN_INVENTORY_SLOT = 35;

    private final List<Integer> candidateVictimSlots = new ArrayList<>();
    private final Map<Integer, Integer> revealedMenuSlotToVictimSlot = new HashMap<>();
    private final Set<Integer> consumedVictimSlots = new HashSet<>();
    private final Set<Integer> forgottenMenuSlots = new HashSet<>();

    private int candidateCursor = 0;
    private int currentRows = START_ROWS;
    private boolean suppressNextInventoryClose;

    /**
     * Rebuilds and shuffles candidate victim inventory slots for a session.
     *
     * @param victim victim user whose inventory is being rummaged
     */
    public void initializeCandidateSlots(PickpocketUser victim) {
        reset();

        Player victimPlayer = victim.getBukkitPlayer();
        if (victimPlayer == null) {
            return;
        }
        for (int slot = MIN_MAIN_INVENTORY_SLOT; slot <= MAX_MAIN_INVENTORY_SLOT; slot++) {
            ItemStack stack = victimPlayer.getInventory().getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            if (PickpocketUtils.isItemTypeDisabled(stack.getType())) {
                continue;
            }
            candidateVictimSlots.add(slot);
        }
        Collections.shuffle(candidateVictimSlots);
    }

    /**
     * Clears all mutable rummage state to defaults.
     */
    public void reset() {
        candidateVictimSlots.clear();
        revealedMenuSlotToVictimSlot.clear();
        consumedVictimSlots.clear();
        forgottenMenuSlots.clear();
        candidateCursor = 0;
        currentRows = START_ROWS;
        suppressNextInventoryClose = false;
    }

    /**
     * @return current menu row count
     */
    public int getCurrentRows() {
        return currentRows;
    }

    /**
     * Sets current menu row count.
     *
     * @param currentRows new row count
     */
    public void setCurrentRows(int currentRows) {
        this.currentRows = currentRows;
    }

    /**
     * @return number of currently revealed menu slots
     */
    public int getRevealedCount() {
        return revealedMenuSlotToVictimSlot.size();
    }

    /**
     * Registers a revealed menu slot mapping to a victim inventory slot.
     *
     * @param menuSlot menu slot index
     * @param victimSlot victim inventory slot index
     */
    public void addRevealedMapping(int menuSlot, int victimSlot) {
        revealedMenuSlotToVictimSlot.put(menuSlot, victimSlot);
        consumedVictimSlots.add(victimSlot);
        forgottenMenuSlots.remove(menuSlot);
    }

    /**
     * @param menuSlot menu slot index
     * @return mapped victim inventory slot or null
     */
    public Integer getVictimSlotForMenuSlot(int menuSlot) {
        return revealedMenuSlotToVictimSlot.get(menuSlot);
    }

    /**
     * Removes a revealed mapping.
     *
     * @param menuSlot menu slot index
     * @return removed victim slot mapping or null
     */
    public Integer removeRevealedMapping(int menuSlot) {
        return revealedMenuSlotToVictimSlot.remove(menuSlot);
    }

    /**
     * @return unmodifiable revealed menu-to-victim mapping
     */
    public Map<Integer, Integer> getRevealedMappings() {
        return Collections.unmodifiableMap(revealedMenuSlotToVictimSlot);
    }

    /**
     * Marks a menu slot as forgotten and no longer revealed.
     *
     * @param menuSlot menu slot index
     */
    public void markMenuSlotForgotten(int menuSlot) {
        revealedMenuSlotToVictimSlot.remove(menuSlot);
        forgottenMenuSlots.add(menuSlot);
    }

    /**
     * @param menuSlot menu slot index
     * @return true if slot has been forgotten
     */
    public boolean isMenuSlotForgotten(int menuSlot) {
        return forgottenMenuSlots.contains(menuSlot);
    }

    /**
     * @return next unused candidate victim slot, or null when exhausted
     */
    public Integer getNextUnusedCandidateVictimSlot() {
        while (candidateCursor < candidateVictimSlots.size()) {
            int victimSlot = candidateVictimSlots.get(candidateCursor++);
            if (!consumedVictimSlots.contains(victimSlot)) {
                return victimSlot;
            }
        }
        return null;
    }

    /**
     * Sets whether the next inventory close should be ignored.
     *
     * @param suppressNextInventoryClose close suppression flag
     */
    public void setSuppressNextInventoryClose(boolean suppressNextInventoryClose) {
        this.suppressNextInventoryClose = suppressNextInventoryClose;
    }

    /**
     * Consumes and resets the suppression flag.
     *
     * @return true if close suppression was consumed
     */
    public boolean consumeSuppressNextInventoryClose() {
        if (!suppressNextInventoryClose) {
            return false;
        }
        suppressNextInventoryClose = false;
        return true;
    }
}
