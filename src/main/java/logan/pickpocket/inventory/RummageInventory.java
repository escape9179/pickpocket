package logan.pickpocket.inventory;

import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.managers.PickpocketSession;
import logan.pickpocket.managers.RummageSessionState;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class RummageInventory {

    private static final String MENU_TITLE = "Rummage";
    private static final String RUMMAGE_BUTTON_TEXT = "Expand Rummage";
    private static final int MAX_ROWS = 6;
    private static final int REVEAL_BASE_COUNT = 1;
    private static final int REVEAL_PER_STAGE = 1;
    private static final float EXPAND_SOUND_BASE_VOLUME = 0.6f;
    private static final float EXPAND_SOUND_STEP_INCREMENT = 0.18f;
    private static final float EXPAND_SOUND_MAX_VOLUME = 1.5f;

    private final PickpocketSession session;
    private final PickpocketUser thief;
    private final PickpocketUser victim;
    private final RummageSessionState state;
    private final Random random = new Random();

    private PlayerInventoryMenu menu;

    public RummageInventory(PickpocketSession session) {
        this.session = session;
        this.thief = session.getThief();
        this.victim = session.getVictim();
        this.state = session.getRummageState();
    }

    public void show() {
        ensureRevealTarget();
        rebuildAndShowMenu(false);
    }

    private void onRummageButtonClick() {
        if (state.getCurrentRows() >= MAX_ROWS) {
            thief.playRummageBlockedSound();
            return;
        }
        int previousRows = state.getCurrentRows();
        state.setCurrentRows(previousRows + 1);
        applyMemoryDecay(previousRows);
        ensureRevealTarget();
        rebuildAndShowMenu(true);
        float expandSoundVolume = getExpandSoundVolume(previousRows);
        thief.playRummageExpandSound(expandSoundVolume);
        victim.playRummageExpandSound(expandSoundVolume);
    }

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
            state.markMenuSlotForgotten(menuSlot);
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
        thief.sendMessage(MessageConfig.getPickpocketSuccessfulMessage());
        refreshSingleSlot(menuSlot);
    }

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

    private void renderMenuContents() {
        int size = menu.getSize();
        int buttonSlot = size - 1;

        for (int slot = 0; slot < size; slot++) {
            if (slot == buttonSlot) {
                continue;
            }
            menu.addItem(slot, createGlassItem());
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

    private void ensureRevealTarget() {
        int targetVisibleCount = getRevealTargetCount();
        while (state.getRevealedCount() < targetVisibleCount) {
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
        }
    }

    private int getRevealTargetCount() {
        int stageIndex = state.getCurrentRows() - 1;
        int revealLevelBonus = thief.getRevealSkill().getRevealLevelBonus();
        return Math.max(0, REVEAL_BASE_COUNT + (stageIndex * REVEAL_PER_STAGE) + revealLevelBonus);
    }

    private float getExpandSoundVolume(int previousRows) {
        int expansionIndex = Math.max(0, previousRows - 1);
        float scaledVolume = EXPAND_SOUND_BASE_VOLUME + (EXPAND_SOUND_STEP_INCREMENT * expansionIndex);
        return Math.min(EXPAND_SOUND_MAX_VOLUME, scaledVolume);
    }

    private void applyMemoryDecay(int previousRows) {
        int stageIndex = state.getCurrentRows() - 1;
        int forgetCount = thief.getMemorySkill().getForgetCount(stageIndex);
        if (forgetCount <= 0) {
            return;
        }

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
        Collections.shuffle(eligibleSlots, random);
        int toForget = Math.min(forgetCount, eligibleSlots.size());
        for (int i = 0; i < toForget; i++) {
            state.markMenuSlotForgotten(eligibleSlots.get(i));
        }
    }

    private Integer getRandomOpenMenuSlot() {
        int size = state.getCurrentRows() * 9;
        int buttonSlot = size - 1;
        List<Integer> freeSlots = new ArrayList<>();
        for (int slot = 0; slot < size; slot++) {
            if (slot == buttonSlot) {
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

    private void refreshSingleSlot(int slot) {
        if (menu == null) {
            return;
        }
        menu.addItem(slot, createGlassItem());
        menu.update();
    }

    private MenuItem createGlassItem() {
        return new MenuItem(ChatColor.WHITE + " ", new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
    }
}
