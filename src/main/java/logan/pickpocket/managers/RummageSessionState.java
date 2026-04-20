package logan.pickpocket.managers;

import logan.pickpocket.main.PickpocketUtils;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mutable per-session board state for rummage.
 */
public final class RummageSessionState {

    public static final int BOARD_SIZE = 54;
    private static final int MIN_MAIN_INVENTORY_SLOT = 9;
    private static final int MAX_MAIN_INVENTORY_SLOT = 35;
    private final BoardCell[] cells = new BoardCell[BOARD_SIZE];

    private boolean suppressNextInventoryClose;

    /**
     * State of a board cell.
     */
    public enum BoardCellState {
        HIDDEN,
        CLUE_REVEALED,
        STEALABLE_REVEALED,
        CLAIMED,
        CLEARED
    }

    /**
     * Batch result for hidden-cell reveals (flood or single).
     */
    public static final class HiddenRevealBatch {
        private final List<Integer> updatedSlots;

        public HiddenRevealBatch(List<Integer> updatedSlots) {
            this.updatedSlots = updatedSlots;
        }

        public List<Integer> getUpdatedSlots() {
            return updatedSlots;
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

        List<Integer> boardSlots = new ArrayList<>(BOARD_SIZE);
        for (int slot = 0; slot < BOARD_SIZE; slot++) {
            boardSlots.add(slot);
        }

        Collections.shuffle(stealableVictimSlots);
        Collections.shuffle(boardSlots);

        int maxAssignments = Math.min(stealableVictimSlots.size(), boardSlots.size());
        for (int index = 0; index < maxAssignments; index++) {
            int boardSlot = boardSlots.get(index);
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
     * Reveals hidden cells from a click using minesweeper semantics.
     *
     * @param boardSlot clicked board index
     * @return updated slots
     */
    public HiddenRevealBatch revealHiddenCells(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return new HiddenRevealBatch(List.of());
        }
        BoardCell start = cells[boardSlot];
        if (start.state != BoardCellState.HIDDEN) {
            return new HiddenRevealBatch(List.of());
        }

        List<Integer> updated = new ArrayList<>();

        if (isBombCell(start)) {
            start.state = BoardCellState.STEALABLE_REVEALED;
            updated.add(boardSlot);
            return new HiddenRevealBatch(updated);
        }

        if (start.adjacentBombCount > 0) {
            start.state = BoardCellState.CLUE_REVEALED;
            updated.add(boardSlot);
            return new HiddenRevealBatch(updated);
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(boardSlot);
        while (!queue.isEmpty()) {
            int slot = queue.remove();
            BoardCell cell = cells[slot];
            if (cell.state != BoardCellState.HIDDEN || isBombCell(cell)) {
                continue;
            }
            if (cell.adjacentBombCount > 0) {
                cell.state = BoardCellState.CLUE_REVEALED;
                updated.add(slot);
                continue;
            }

            cell.state = BoardCellState.CLEARED;
            updated.add(slot);
            for (int neighborSlot : neighbors8(slot)) {
                BoardCell neighbor = cells[neighborSlot];
                if (neighbor.state != BoardCellState.HIDDEN || isBombCell(neighbor)) {
                    continue;
                }
                if (neighbor.adjacentBombCount > 0) {
                    neighbor.state = BoardCellState.CLUE_REVEALED;
                    updated.add(neighborSlot);
                    continue;
                }
                queue.add(neighborSlot);
            }
        }
        return new HiddenRevealBatch(updated);
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
            cell.state = cell.adjacentBombCount > 0
                    ? BoardCellState.CLUE_REVEALED
                    : BoardCellState.CLEARED;
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
     * @return adjacent stealable (bomb) count for clue rendering
     */
    public int getAdjacentBombCount(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return 0;
        }
        return cells[boardSlot].adjacentBombCount;
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
            cells[slot].adjacentBombCount = countAdjacentBombCells(slot);
        }
    }

    private int countAdjacentBombCells(int slot) {
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
                if (isBombCell(cells[neighborSlot])) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < BOARD_SIZE;
    }

    private static boolean isBombCell(BoardCell cell) {
        return cell.victimInventorySlot != null;
    }

    private static final class BoardCell {
        private BoardCellState state = BoardCellState.HIDDEN;
        private Integer victimInventorySlot;
        private int adjacentBombCount;
    }
}
