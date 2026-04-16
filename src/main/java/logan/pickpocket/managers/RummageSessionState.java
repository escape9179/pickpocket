package logan.pickpocket.managers;

import logan.pickpocket.main.PickpocketUtils;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        CLAIMED
    }

    /**
     * Reveal outcome for hidden-cell clicks.
     */
    public enum RevealResult {
        NONE,
        CLUE,
        STEALABLE,
        TRAP
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

        ItemStack[] trapContents = victim.getTrapContentsSnapshot();
        for (int slot = 0; slot < BOARD_SIZE; slot++) {
            ItemStack trapStack = slot < trapContents.length ? trapContents[slot] : null;
            if (trapStack == null || trapStack.getType() == Material.AIR || trapStack.getAmount() <= 0) {
                continue;
            }
            double chance = Math.min(1.0d, trapStack.getAmount() * TRAP_CHANCE_PER_ITEM);
            if (random.nextDouble() >= chance) {
                continue;
            }
            ItemStack trapPreview = trapStack.clone();
            trapPreview.setAmount(1);
            cells[slot].trapItem = trapPreview;
        }

        Collections.shuffle(stealableVictimSlots);
        List<Integer> availableBoardSlots = new ArrayList<>();
        for (int slot = 0; slot < BOARD_SIZE; slot++) {
            if (cells[slot].trapItem == null) {
                availableBoardSlots.add(slot);
            }
        }
        Collections.shuffle(availableBoardSlots);

        int maxAssignments = Math.min(stealableVictimSlots.size(), availableBoardSlots.size());
        for (int index = 0; index < maxAssignments; index++) {
            int boardSlot = availableBoardSlots.get(index);
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
     * Reveals a hidden board slot.
     *
     * @param boardSlot board index
     * @return reveal outcome
     */
    public RevealResult revealHiddenSlot(int boardSlot) {
        if (!isValidSlot(boardSlot)) {
            return RevealResult.NONE;
        }
        BoardCell cell = cells[boardSlot];
        if (cell.state != BoardCellState.HIDDEN) {
            return RevealResult.NONE;
        }
        if (cell.trapItem != null) {
            cell.state = BoardCellState.TRAP_REVEALED;
            return RevealResult.TRAP;
        }
        if (cell.victimInventorySlot != null) {
            cell.state = BoardCellState.STEALABLE_REVEALED;
            return RevealResult.STEALABLE;
        }
        cell.state = BoardCellState.CLUE_REVEALED;
        return RevealResult.CLUE;
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
        private int adjacentStealableCount;
    }
}
