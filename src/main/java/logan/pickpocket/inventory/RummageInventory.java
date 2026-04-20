package logan.pickpocket.inventory;

import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.api.util.HeadUtils;
import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.managers.PickpocketSession;
import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.managers.RummageSessionState;
import logan.pickpocket.managers.SessionEndReason;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Handles rendering and interaction for the rummage inventory UI.
 */
public final class RummageInventory {

    private static final String MENU_TITLE = "Rummage";
    private static final int BOARD_ROWS = 6;
    private static final float CHANCE_LOSS_BASE_VOLUME = 0.7f;
    private static final float CHANCE_LOSS_STEP_VOLUME = 0.25f;
    private static final float CHANCE_LOSS_MAX_VOLUME = 1.5f;

    private final PickpocketSession session;
    private final PickpocketUser thief;
    private final PickpocketUser victim;
    private final RummageSessionState state;

    private PlayerInventoryMenu menu;
    private int successfulStealsThisSession;

    /**
     * Creates a rummage inventory view bound to a pickpocket session.
     *
     * @param session active pickpocket session
     */
    public RummageInventory(PickpocketSession session) {
        this.session = session;
        this.thief = session.getThief();
        this.victim = session.getVictim();
        this.state = session.getRummageState();
    }

    /**
     * Opens the rummage menu for the thief.
     */
    public void show() {
        rebuildAndShowMenu();
    }

    /**
     * Handles clicks on revealed stealable items.
     *
     * @param menuSlot clicked menu slot
     */
    private void onRevealedItemClick(int menuSlot) {
        Player thiefPlayer = thief.getBukkitPlayer();
        Player victimPlayer = victim.getBukkitPlayer();
        if (thiefPlayer == null || victimPlayer == null || !victimPlayer.isOnline()) {
            thief.sendMessage(MessageConfig.getTargetUnavailableMessage());
            state.clearStealableMapping(menuSlot);
            refreshEntireBoard();
            checkNoClickableSlotsRemaining();
            return;
        }

        Integer victimSlot = state.getVictimSlotForBoardSlot(menuSlot);
        if (victimSlot == null) {
            return;
        }

        ItemStack victimItem = victimPlayer.getInventory().getItem(victimSlot);
        if (victimItem == null || victimItem.getType() == Material.AIR) {
            state.clearStealableMapping(menuSlot);
            refreshEntireBoard();
            checkNoClickableSlotsRemaining();
            return;
        }

        completeTransfer(menuSlot, victimSlot);
    }

    private void completeTransfer(int menuSlot, int expectedVictimSlot) {
        if (!isSessionStillRummaging()) {
            return;
        }

        Player thiefPlayer = thief.getBukkitPlayer();
        Player victimPlayer = victim.getBukkitPlayer();
        if (thiefPlayer == null || victimPlayer == null || !victimPlayer.isOnline()) {
            if (thiefPlayer != null) {
                thief.sendMessage(MessageConfig.getTargetUnavailableMessage());
            }
            state.clearStealableMapping(menuSlot);
            refreshEntireBoard();
            checkNoClickableSlotsRemaining();
            return;
        }

        Integer mappedVictimSlot = state.getVictimSlotForBoardSlot(menuSlot);
        if (mappedVictimSlot == null || mappedVictimSlot != expectedVictimSlot) {
            refreshSingleSlot(menuSlot);
            return;
        }

        ItemStack victimItem = victimPlayer.getInventory().getItem(expectedVictimSlot);
        if (victimItem == null || victimItem.getType() == Material.AIR) {
            state.clearStealableMapping(menuSlot);
            refreshEntireBoard();
            checkNoClickableSlotsRemaining();
            return;
        }

        ItemStack stolenItem = victimItem.clone();
        victimPlayer.getInventory().setItem(expectedVictimSlot, new ItemStack(Material.AIR));
        Map<Integer, ItemStack> overflow = thiefPlayer.getInventory().addItem(stolenItem);
        if (!overflow.isEmpty()) {
            overflow.values().forEach(item -> thiefPlayer.getWorld().dropItemNaturally(thiefPlayer.getLocation(), item));
        }

        state.markClaimed(menuSlot);
        thief.setSteals(thief.getSteals() + 1);
        thief.incrementPredatorSuccesses();
        thief.addTotalItemsStolen(1);
        victim.incrementVictimCount();
        thief.save();
        victim.save();
        thief.playStealSuccessSound();
        successfulStealsThisSession++;
        playChanceLossSound(successfulStealsThisSession);
        refreshEntireBoard();
        checkNoClickableSlotsRemaining();
    }

    private boolean isSessionStillRummaging() {
        PickpocketSession activeSession = PickpocketSessionManager.getSession(thief);
        return activeSession != null
                && activeSession == session
                && activeSession.isRummaging();
    }

    /**
     * Rebuilds menu contents and shows the inventory to the thief.
     */
    private void rebuildAndShowMenu() {
        Player thiefPlayer = thief.getBukkitPlayer();
        if (thiefPlayer == null) {
            return;
        }

        menu = new PlayerInventoryMenu(MENU_TITLE, BOARD_ROWS);
        renderMenuContents();
        session.setRummageInventory(this);
        menu.show(thiefPlayer);
    }

    /**
     * Renders all board slots based on cell state.
     */
    private void renderMenuContents() {
        for (int slot = 0; slot < RummageSessionState.BOARD_SIZE; slot++) {
            menu.addItem(slot, createMenuItemForSlot(slot));
        }
        menu.update();
    }

    /**
     * Handles hidden-slot reveal flow.
     */
    private void onHiddenSlotClick(int menuSlot) {
        RummageSessionState.HiddenRevealBatch batch = state.revealHiddenCells(menuSlot);
        List<Integer> updated = batch.getUpdatedSlots();
        if (updated.isEmpty()) {
            return;
        }
        refreshSlots(updated);
        checkNoClickableSlotsRemaining();
    }

    private void playChanceLossSound(int usedChances) {
        float volume = Math.min(CHANCE_LOSS_MAX_VOLUME,
                CHANCE_LOSS_BASE_VOLUME + (Math.max(0, usedChances - 1) * CHANCE_LOSS_STEP_VOLUME));
        Player thiefPlayer = thief.getBukkitPlayer();
        Player victimPlayer = victim.getBukkitPlayer();
        if (thiefPlayer != null) {
            thiefPlayer.playSound(thiefPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, volume, 0.75f);
        }
        if (victimPlayer != null) {
            victimPlayer.playSound(victimPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, volume, 0.75f);
        }
    }

    private void checkNoClickableSlotsRemaining() {
        if (!state.hasClickableSlotsRemaining()) {
            thief.sendMessage(MessageConfig.getNothingElseHereMessage());
            closeInventoryAndEnd(SessionEndReason.NO_CLICKABLE_SLOTS_REMAINING);
        }
    }

    private void closeInventoryAndEnd(SessionEndReason endReason) {
        PickpocketSessionManager.unlinkSession(thief, endReason);
        Player thiefPlayer = thief.getBukkitPlayer();
        if (thiefPlayer != null) {
            thiefPlayer.closeInventory();
        }
    }

    /**
     * Renders a single slot and updates the menu.
     *
     * @param slot menu slot index
     */
    private void refreshSingleSlot(int slot) {
        if (menu == null) {
            return;
        }
        menu.addItem(slot, createMenuItemForSlot(slot));
        menu.update();
    }

    private void refreshSlots(Iterable<Integer> slots) {
        if (menu == null) {
            return;
        }
        for (int slot : slots) {
            menu.addItem(slot, createMenuItemForSlot(slot));
        }
        menu.update();
    }

    private void refreshEntireBoard() {
        if (menu == null) {
            return;
        }
        renderMenuContents();
    }

    private MenuItem createMenuItemForSlot(int menuSlot) {
        RummageSessionState.BoardCellState boardCellState = state.getCellState(menuSlot);
        if (boardCellState == RummageSessionState.BoardCellState.CLEARED) {
            return new MenuItem(new ItemStack(Material.AIR));
        }
        if (boardCellState == RummageSessionState.BoardCellState.CLAIMED) {
            return new MenuItem(new ItemStack(Material.AIR));
        }
        if (boardCellState == RummageSessionState.BoardCellState.HIDDEN) {
            if (PickpocketSessionManager.isDebugRevealEnabled()) {
                return createDebugPreviewForHiddenSlot(menuSlot);
            }
            return clickable(
                    new MenuItem(ChatColor.WHITE + " ", new ItemStack(Material.WHITE_STAINED_GLASS_PANE)),
                    () -> onHiddenSlotClick(menuSlot));
        }
        if (boardCellState == RummageSessionState.BoardCellState.CLUE_REVEALED) {
            if (state.getAdjacentBombCount(menuSlot) <= 0) {
                return new MenuItem(new ItemStack(Material.AIR));
            }
            return createClueItem(menuSlot);
        }
        if (boardCellState == RummageSessionState.BoardCellState.STEALABLE_REVEALED) {
            Integer victimSlot = state.getVictimSlotForBoardSlot(menuSlot);
            Player victimPlayer = victim.getBukkitPlayer();
            if (victimSlot == null || victimPlayer == null) {
                state.clearStealableMapping(menuSlot);
                return createMenuItemForSlot(menuSlot);
            }
            ItemStack stack = victimPlayer.getInventory().getItem(victimSlot);
            if (stack == null || stack.getType() == Material.AIR) {
                state.clearStealableMapping(menuSlot);
                return createMenuItemForSlot(menuSlot);
            }
            return clickable(new MenuItem(stack), () -> onRevealedItemClick(menuSlot));
        }
        return new MenuItem(new ItemStack(Material.AIR));
    }

    private MenuItem createDebugPreviewForHiddenSlot(int menuSlot) {
        Integer victimSlot = state.getVictimSlotForBoardSlot(menuSlot);
        Player victimPlayer = victim.getBukkitPlayer();
        if (victimSlot != null && victimPlayer != null) {
            ItemStack stack = victimPlayer.getInventory().getItem(victimSlot);
            if (stack != null && stack.getType() != Material.AIR) {
                return clickable(new MenuItem(stack), () -> onHiddenSlotClick(menuSlot));
            }
        }
        return clickable(createClueItem(menuSlot), () -> onHiddenSlotClick(menuSlot));
    }

    private MenuItem createClueItem(int menuSlot) {
        int adjacentCount = state.getAdjacentBombCount(menuSlot);
        int digit = Math.max(0, Math.min(9, adjacentCount));
        ItemStack clueHead = HeadUtils.numberHead(digit);
        ItemMeta meta = clueHead.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + String.valueOf(adjacentCount));
            clueHead.setItemMeta(meta);
        }
        return new MenuItem(clueHead);
    }

    private MenuItem clickable(MenuItem item, Runnable action) {
        item.addListener(event -> action.run());
        return item;
    }
}
