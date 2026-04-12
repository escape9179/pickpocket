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

    public void reset() {
        candidateVictimSlots.clear();
        revealedMenuSlotToVictimSlot.clear();
        consumedVictimSlots.clear();
        forgottenMenuSlots.clear();
        candidateCursor = 0;
        currentRows = START_ROWS;
        suppressNextInventoryClose = false;
    }

    public int getCurrentRows() {
        return currentRows;
    }

    public void setCurrentRows(int currentRows) {
        this.currentRows = currentRows;
    }

    public int getRevealedCount() {
        return revealedMenuSlotToVictimSlot.size();
    }

    public void addRevealedMapping(int menuSlot, int victimSlot) {
        revealedMenuSlotToVictimSlot.put(menuSlot, victimSlot);
        consumedVictimSlots.add(victimSlot);
        forgottenMenuSlots.remove(menuSlot);
    }

    public Integer getVictimSlotForMenuSlot(int menuSlot) {
        return revealedMenuSlotToVictimSlot.get(menuSlot);
    }

    public Integer removeRevealedMapping(int menuSlot) {
        return revealedMenuSlotToVictimSlot.remove(menuSlot);
    }

    public Map<Integer, Integer> getRevealedMappings() {
        return Collections.unmodifiableMap(revealedMenuSlotToVictimSlot);
    }

    public void markMenuSlotForgotten(int menuSlot) {
        revealedMenuSlotToVictimSlot.remove(menuSlot);
        forgottenMenuSlots.add(menuSlot);
    }

    public boolean isMenuSlotForgotten(int menuSlot) {
        return forgottenMenuSlots.contains(menuSlot);
    }

    public Integer getNextUnusedCandidateVictimSlot() {
        while (candidateCursor < candidateVictimSlots.size()) {
            int victimSlot = candidateVictimSlots.get(candidateCursor++);
            if (!consumedVictimSlots.contains(victimSlot)) {
                return victimSlot;
            }
        }
        return null;
    }

    public void setSuppressNextInventoryClose(boolean suppressNextInventoryClose) {
        this.suppressNextInventoryClose = suppressNextInventoryClose;
    }

    public boolean consumeSuppressNextInventoryClose() {
        if (!suppressNextInventoryClose) {
            return false;
        }
        suppressNextInventoryClose = false;
        return true;
    }
}
