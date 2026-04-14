package logan.pickpocket.inventory;

import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.pickpocket.managers.PickpocketSession;
import logan.pickpocket.managers.RummageSessionState;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Handles rendering and interaction for the rummage inventory UI.
 */
public final class RummageInventory {

    private static final String MENU_TITLE = "Rummage";
    private static final String RUMMAGE_BUTTON_TEXT = "Expand Rummage";
    private static final int MAX_ROWS = 6;
    private static final float EXPAND_SOUND_BASE_VOLUME = 0.6f;
    private static final float EXPAND_SOUND_STEP_INCREMENT = 0.18f;
    private static final float EXPAND_SOUND_MAX_VOLUME = 1.5f;

    private final PickpocketSession session;
    private final PickpocketUser thief;
    private final PickpocketUser victim;
    private final RummageSessionState state;
    private final Random random = new Random();

    private PlayerInventoryMenu menu;

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
     * Reveals items for the initial row and opens the rummage menu for the thief.
     */
    public void show() {
        revealForActiveRow();
        rebuildAndShowMenu(false);
    }

    /**
     * Expands the rummage menu by one row, applies memory decay, and reveals items for the new row.
     */
    private void onRummageButtonClick() {
        if (state.getCurrentRows() >= MAX_ROWS) {
            thief.playRummageBlockedSound();
            return;
        }
        int previousRows = state.getCurrentRows();
        state.setCurrentRows(previousRows + 1);
        state.recordRummageExpand(previousRows);
        applyMemoryDecay(previousRows);
        revealForActiveRow();
        rebuildAndShowMenu(true);
        float expandSoundVolume = getExpandSoundVolume(previousRows);
        thief.playRummageExpandSound(expandSoundVolume);
        victim.playRummageExpandSound(expandSoundVolume);
    }

    /**
     * Transfers a clicked revealed item from victim inventory to thief inventory.
     *
     * @param menuSlot clicked menu slot
     */
    private void onRevealedItemClick(int menuSlot) {
        Player thiefPlayer = thief.getBukkitPlayer();
        Player victimPlayer = victim.getBukkitPlayer();
        if (thiefPlayer == null || victimPlayer == null || !victimPlayer.isOnline()) {
            thief.sendMessage(ChatColor.RED + "Player is no longer available.");
            state.markMenuSlotForgotten(menuSlot);
            refreshSingleSlot(menuSlot);
            return;
        }

        Integer victimSlot = state.getVictimSlotForMenuSlot(menuSlot);
        if (victimSlot == null) {
            return;
        }

        ItemStack victimItem = victimPlayer.getInventory().getItem(victimSlot);
        if (victimItem == null || victimItem.getType() == Material.AIR) {
            state.removeRevealedMapping(menuSlot);
            refreshSingleSlot(menuSlot);
            return;
        }

        ItemStack stolenItem = victimItem.clone();
        victimPlayer.getInventory().setItem(victimSlot, new ItemStack(Material.AIR));
        Map<Integer, ItemStack> overflow = thiefPlayer.getInventory().addItem(stolenItem);
        if (!overflow.isEmpty()) {
            overflow.values().forEach(item -> thiefPlayer.getWorld().dropItemNaturally(thiefPlayer.getLocation(), item));
        }

        state.markMenuSlotForgotten(menuSlot);
        thief.setSteals(thief.getSteals() + 1);
        thief.incrementPredatorSuccesses();
        thief.addTotalItemsStolen(1);
        victim.incrementVictimCount();
        thief.save();
        victim.save();
        thief.playStealSuccessSound();
        refreshSingleSlot(menuSlot);
    }

    /**
     * Rebuilds menu contents and shows the inventory to the thief.
     *
     * @param replacingOpenMenu whether an open menu is being replaced
     */
    private void rebuildAndShowMenu(boolean replacingOpenMenu) {
        Player thiefPlayer = thief.getBukkitPlayer();
        if (thiefPlayer == null) {
            return;
        }

        if (menu != null && replacingOpenMenu && menu.getViewer() != null) {
            state.setSuppressNextInventoryClose(true);
            menu.close();
        }

        menu = new PlayerInventoryMenu(MENU_TITLE, state.getCurrentRows());
        renderMenuContents();
        session.setRummageInventory(this);
        menu.show(thiefPlayer);
    }

    /**
     * Renders filler panes, revealed items, and expansion button.
     */
    private void renderMenuContents() {
        int size = menu.getSize();
        int buttonSlot = size - 1;

        for (int slot = 0; slot < size; slot++) {
            if (slot == buttonSlot) {
                continue;
            }
            menu.addItem(slot, createFillerMenuItem(slot));
        }

        for (Map.Entry<Integer, Integer> mapping : new ArrayList<>(state.getRevealedMappings().entrySet())) {
            int menuSlot = mapping.getKey();
            if (menuSlot >= size) {
                continue;
            }
            Player victimPlayer = victim.getBukkitPlayer();
            if (victimPlayer == null) {
                continue;
            }
            ItemStack currentVictimItem = victimPlayer.getInventory().getItem(mapping.getValue());
            if (currentVictimItem == null || currentVictimItem.getType() == Material.AIR) {
                state.removeRevealedMapping(menuSlot);
                continue;
            }
            MenuItem menuItem = new MenuItem(currentVictimItem);
            menuItem.addListener(event -> onRevealedItemClick(menuSlot));
            menu.addItem(menuSlot, menuItem);
        }

        MenuItem rummageButton = new MenuItem(RUMMAGE_BUTTON_TEXT, new ItemStack(Material.CHEST));
        rummageButton.addListener(event -> onRummageButtonClick());
        menu.addItem(buttonSlot, rummageButton);
        menu.update();
    }

    /**
     * Reveals victim items for the currently active row only.
     */
    private void revealForActiveRow() {
        int revealsPerRow = thief.getRevealSkill().getRevealedSlotsPerMenuRow();
        int revealedForRow = 0;
        while (revealedForRow < revealsPerRow) {
            Integer menuSlot = getRandomOpenMenuSlot();
            if (menuSlot == null) {
                return;
            }

            Integer victimSlot = state.getNextUnusedCandidateVictimSlot();
            if (victimSlot == null) {
                return;
            }

            Player victimPlayer = victim.getBukkitPlayer();
            if (victimPlayer == null) {
                return;
            }
            ItemStack stack = victimPlayer.getInventory().getItem(victimSlot);
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            state.addRevealedMapping(menuSlot, victimSlot);
            revealedForRow++;
        }
    }

    /**
     * Computes expansion sound volume based on previous menu size.
     *
     * @param previousRows row count before expansion
     * @return clamped volume for expansion sounds
     */
    private float getExpandSoundVolume(int previousRows) {
        int expansionIndex = Math.max(0, previousRows - 1);
        float scaledVolume = EXPAND_SOUND_BASE_VOLUME + (EXPAND_SOUND_STEP_INCREMENT * expansionIndex);
        return Math.min(EXPAND_SOUND_MAX_VOLUME, scaledVolume);
    }

    /**
     * Applies memory skill decay to previously revealed slots.
     *
     * @param previousRows row count before expansion
     */
    private void applyMemoryDecay(int previousRows) {
        int memoryLevel = thief.getMemorySkill().getLevel();
        int previousSize = previousRows * 9;
        List<Integer> eligibleSlots = new ArrayList<>();
        for (Integer menuSlot : state.getRevealedMappings().keySet()) {
            if (menuSlot < previousSize) {
                eligibleSlots.add(menuSlot);
            }
        }
        if (eligibleSlots.isEmpty()) {
            return;
        }

        if (memoryLevel == 0) {
            for (Integer menuSlot : eligibleSlots) {
                state.markMenuSlotForgotten(menuSlot);
            }
            return;
        }

        double forgetChance = thief.getMemorySkill().getForgetChance();
        for (Integer menuSlot : eligibleSlots) {
            if (random.nextDouble() < forgetChance) {
                state.markMenuSlotForgotten(menuSlot);
            }
        }
    }

    /**
     * @return random eligible menu slot for a new reveal, or null when unavailable
     */
    private Integer getRandomOpenMenuSlot() {
        int size = state.getCurrentRows() * 9;
        int buttonSlot = size - 1;
        List<Integer> freeSlots = new ArrayList<>();
        for (int slot = 0; slot < size; slot++) {
            if (slot == buttonSlot) {
                continue;
            }
            if (slot < state.getFirstMenuSlotOpenForNewReveals()) {
                continue;
            }
            if (state.isDeadExpandMenuSlot(slot)) {
                continue;
            }
            if (state.getVictimSlotForMenuSlot(slot) != null) {
                continue;
            }
            if (state.isMenuSlotForgotten(slot)) {
                continue;
            }
            freeSlots.add(slot);
        }
        if (freeSlots.isEmpty()) {
            return null;
        }
        return freeSlots.get(random.nextInt(freeSlots.size()));
    }

    /**
     * Renders a single slot as filler glass and updates the menu.
     *
     * @param slot menu slot index
     */
    private void refreshSingleSlot(int slot) {
        if (menu == null) {
            return;
        }
        menu.addItem(slot, createFillerMenuItem(slot));
        menu.update();
    }

    /**
     * @return filler pane for a menu slot (dead expand, forgotten, or default white)
     */
    private MenuItem createFillerMenuItem(int menuSlot) {
        if (state.isDeadExpandMenuSlot(menuSlot)) {
            return new MenuItem(ChatColor.WHITE + " ", new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        if (state.isMenuSlotForgotten(menuSlot)) {
            return new MenuItem(ChatColor.WHITE + " ", new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }
        return new MenuItem(ChatColor.WHITE + " ", new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
    }
}
