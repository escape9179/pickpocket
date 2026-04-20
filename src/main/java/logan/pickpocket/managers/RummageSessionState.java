package logan.pickpocket.managers;

import logan.pickpocket.inventory.PickpocketInventoryBlueprint;
import logan.pickpocket.main.PickpocketUtils;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Mutable per-session board state for rummage.
 */
public final class RummageSessionState {

    public static final int BOARD_SIZE = 54;
    private static final int MIN_MAIN_INVENTORY_SLOT = 9;
    private static final int MAX_MAIN_INVENTORY_SLOT = 35;
    private static final double TRAP_CHANCE_PER_ITEM = 0.0064d;

    private final BoardCell[] cells = new BoardCell[BOARD_SIZE];
    private final Random random = new Random();

    private boolean suppressNextInventoryClose;

    /**
     * State of a board cell.
     */
    public enum BoardCellState {
        HIDDEN,
        CLUE_REVEALED,
        STEALABLE_REVEALED,
        TRAP_REVEALED,
        CLAIMED,
        CLEARED
    }

    /**
     * Batch result for hidden-cell reveals (flood or single).
     */
    public static final class HiddenRevealBatch {
        private final List<Integer> updatedSlots;
        private final boolean trapTriggered;

        public HiddenRevealBatch(List<Integer> updatedSlots, boolean trapTriggered) {
            this.updatedSlots = updatedSlots;
            this.trapTriggered = trapTriggered;
        }

        public List<Integer> getUpdatedSlots() {
            return updatedSlots;
        }

        public boolean isTrapTriggered() {
            return trapTriggered;
        }
    }

    /**
     * Rebuilds and randomizes the rummage board for a session.
     *
     * @param victim victim user whose inventory is being rummaged
     */
    public void initializeBoard(PickpocketUser victim) {
        reset();

        Player victimPlayer = victim.getBukkitPlayer();
        if (victimPlayer == null) {
            return;
        }

        ItemStack[] blueprint = victim.getPickpocketInventorySnapshot();

        List<Integer> pureGreenBoardSlots = new ArrayList<>();
        for (int slot = 0; slot < BOARD_SIZE; slot++) {
            ItemStack marker = blueprint[slot];
            PickpocketInventoryBlueprint.SlotKind kind = PickpocketInventoryBlueprint.kindFromItem(marker);
            switch (kind) {
                case EMPTY -> cells[slot].floodEmptyAfterInit = true;
                case HINT -> { }
                case STEALABLE -> pureGreenBoardSlots.add(slot);
                case TRAP_ITEM -> {
                    double chance = Math.min(1.0d, marker.getAmount() * TRAP_CHANCE_PER_ITEM);
                    if (random.nextDouble() < chance) {
                        ItemStack trapPreview = marker.clone();
                        trapPreview.setAmount(1);
                        cells[slot].trapItem = trapPreview;
                    } else {
                        cells[slot].floodEmptyAfterInit = true;
                    }
                }
            }
        }

        List<Integer> stealableVictimSlots = new ArrayList<>();
        for (int slot = MIN_MAIN_INVENTORY_SLOT; slot <= MAX_MAIN_INVENTORY_SLOT; slot++) {
            ItemStack stack = victimPlayer.getInventory().getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            if (PickpocketUtils.isItemTypeDisabled(stack.getType())) {
                continue;
            }
            stealableVictimSlots.add(slot);
        }

        Collections.shuffle(stealableVictimSlots);
        Collections.shuffle(pureGreenBoardSlots);

        int maxAssignments = Math.min(stealableVictimSlots.size(), pureGreenBoardSlots.size());
        for (int index = 0; index < maxAssignments; index++) {
            int boardSlot = pureGreenBoardSlots.get(index);
            int victimSlot = stealableVictimSlots.get(index);
            cells[boardSlot].victimInventorySlot = victimSlot;
        }

        recomputeAdjacencyClues();
    }

    /**
     * Clears all mutable rummage state to defaults.
     */
    public void reset() {
        for (int slot = 0; slot < BOARD_SIZE; slot++) {
            cells[slot] = new BoardCell();
        }
        suppressNextInventoryClose = false;
    }

    /**
     * Reveals hidden cells from a click (Minesweeper flood for {@link BoardCell#floodEmptyAfterInit}).
     *
     * @param boardSlot clicked board index
     * @return updated slots and whether a trap was revealed
     */
    public HiddenRevealBatch revealHiddenCells(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return new HiddenRevealBatch(List.of(), false);
        }
        BoardCell start = cells[boardSlot];
        if (start.state != BoardCellState.HIDDEN) {
            return new HiddenRevealBatch(List.of(), false);
        }

        List<Integer> updated = new ArrayList<>();
        boolean trapTriggered = false;

        if (start.trapItem != null) {
            start.state = BoardCellState.TRAP_REVEALED;
            updated.add(boardSlot);
            return new HiddenRevealBatch(updated, true);
        }
        if (start.victimInventorySlot != null) {
            start.state = BoardCellState.STEALABLE_REVEALED;
            updated.add(boardSlot);
            return new HiddenRevealBatch(updated, false);
        }
        if (start.floodEmptyAfterInit) {
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(boardSlot);
            while (!queue.isEmpty()) {
                int s = queue.remove();
                BoardCell cell = cells[s];
                if (cell.state != BoardCellState.HIDDEN) {
                    continue;
                }
                if (cell.trapItem != null || cell.victimInventorySlot != null) {
                    trapTriggered |= revealBoundaryCell(s, updated);
                    continue;
                }
                if (!cell.floodEmptyAfterInit) {
                    trapTriggered |= revealBoundaryCell(s, updated);
                    continue;
                }
                cell.state = BoardCellState.CLEARED;
                updated.add(s);
                for (int n : neighbors8(s)) {
                    BoardCell neighbor = cells[n];
                    if (neighbor.state != BoardCellState.HIDDEN) {
                        continue;
                    }
                    if (neighbor.floodEmptyAfterInit
                            && neighbor.trapItem == null
                            && neighbor.victimInventorySlot == null) {
                        queue.add(n);
                    } else {
                        trapTriggered |= revealBoundaryCell(n, updated);
                    }
                }
            }
            return new HiddenRevealBatch(updated, trapTriggered);
        }

        trapTriggered = revealBoundaryCell(boardSlot, updated);
        return new HiddenRevealBatch(updated, trapTriggered);
    }

    private boolean revealBoundaryCell(int boardSlot, List<Integer> updated) {
        if (!isValidSlot(boardSlot)) {
            return false;
        }
        BoardCell cell = cells[boardSlot];
        if (cell.state != BoardCellState.HIDDEN) {
            return false;
        }
        if (cell.trapItem != null) {
            cell.state = BoardCellState.TRAP_REVEALED;
            updated.add(boardSlot);
            return true;
        }
        if (cell.victimInventorySlot != null) {
            cell.state = BoardCellState.STEALABLE_REVEALED;
            updated.add(boardSlot);
            return false;
        }
        cell.state = BoardCellState.CLUE_REVEALED;
        updated.add(boardSlot);
        return false;
    }

    private static int[] neighbors8(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        int[] out = new int[8];
        int i = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int nr = row + dr;
                int nc = col + dc;
                if (nr < 0 || nr >= 6 || nc < 0 || nc >= 9) {
                    continue;
                }
                out[i++] = nr * 9 + nc;
            }
        }
        return java.util.Arrays.copyOf(out, i);
    }

    /**
     * Marks a revealed stealable cell as claimed.
     *
     * @param boardSlot board index
     */
    public void markClaimed(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return;
        }
        BoardCell cell = cells[boardSlot];
        cell.state = BoardCellState.CLAIMED;
        cell.victimInventorySlot = null;
    }

    /**
     * Clears stale stealable mapping from a board slot.
     *
     * @param boardSlot board index
     */
    public void clearStealableMapping(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return;
        }
        BoardCell cell = cells[boardSlot];
        cell.victimInventorySlot = null;
        if (cell.state == BoardCellState.STEALABLE_REVEALED) {
            cell.state = BoardCellState.CLUE_REVEALED;
        }
    }

    /**
     * @param boardSlot board index
     * @return board cell state for the slot
     *
     * @throws IllegalArgumentException when slot is out of range
     */
    public BoardCellState getCellState(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            throw new IllegalArgumentException("Invalid board slot: " + boardSlot);
        }
        return cells[boardSlot].state;
    }

    /**
     * @param boardSlot board index
     * @return mapped victim inventory slot for a stealable cell, or null
     */
    public Integer getVictimSlotForBoardSlot(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return null;
        }
        return cells[boardSlot].victimInventorySlot;
    }

    /**
     * @param boardSlot board index
     * @return trap preview item for this slot, or null
     */
    public ItemStack getTrapItem(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return null;
        }
        ItemStack trapItem = cells[boardSlot].trapItem;
        return trapItem == null ? null : trapItem.clone();
    }

    /**
     * @param boardSlot board index
     * @return adjacent stealable count for clue rendering
     */
    public int getAdjacentStealableCount(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return 0;
        }
        return cells[boardSlot].adjacentStealableCount;
    }

    /**
     * @return true when at least one cell can still be interacted with
     */
    public boolean hasClickableSlotsRemaining() {
        for (BoardCell cell : cells) {
            if (cell.state == BoardCellState.HIDDEN || cell.state == BoardCellState.STEALABLE_REVEALED) {
                return true;
            }
        }
        return false;
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

    private void recomputeAdjacencyClues() {
        for (int slot = 0; slot < BOARD_SIZE; slot++) {
            cells[slot].adjacentStealableCount = countAdjacentStealableCells(slot);
        }
    }

    private int countAdjacentStealableCells(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        int count = 0;
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }
                int nextRow = row + rowOffset;
                int nextCol = col + colOffset;
                if (nextRow < 0 || nextRow >= 6 || nextCol < 0 || nextCol >= 9) {
                    continue;
                }
                int neighborSlot = (nextRow * 9) + nextCol;
                if (cells[neighborSlot].victimInventorySlot != null) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < BOARD_SIZE;
    }

    private static final class BoardCell {
        private BoardCellState state = BoardCellState.HIDDEN;
        private Integer victimInventorySlot;
        private ItemStack trapItem;
        private boolean floodEmptyAfterInit;
        private int adjacentStealableCount;
    }
}
