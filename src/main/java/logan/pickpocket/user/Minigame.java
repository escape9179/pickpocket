package logan.pickpocket.user;

import logan.api.gui.MenuItem;
import logan.api.gui.MenuItemClickEvent;
import logan.api.gui.PlayerInventoryMenu;
import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.config.Config;
import logan.pickpocket.hooks.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Minigame {

    private static final int INVENTORY_SIZE = 36;

    private final PickpocketUser predatorUser;
    private final PickpocketUser victimUser;
    private final ItemStack item;
    private final PlayerInventoryMenu gui;
    private final AtomicInteger correctClicks = new AtomicInteger(0);

    public Minigame(PickpocketUser predatorUser, PickpocketUser victimUser, ItemStack item) {
        this.predatorUser = predatorUser;
        this.victimUser = victimUser;
        this.item = item;
        this.gui = new PlayerInventoryMenu(
                (victimUser.getBukkitPlayer() != null ? victimUser.getBukkitPlayer().getName() : "unknown")
                        + "'s Inventory",
                INVENTORY_SIZE / 9);
    }

    public PickpocketUser getPredatorUser() {
        return predatorUser;
    }

    private void setupGUI(Inventory inventory) {
        Map<Integer, MenuItem> menuItemMap = createMinigameMenuItems(inventory);
        menuItemMap.forEach(gui::addItem);
    }

    private void showGUI() {
        Player predator = predatorUser.getBukkitPlayer();
        if (predator != null) {
            predator.closeInventory();
        }
        gui.show(predator);
    }

    public void start(Inventory inventory) {
        setupGUI(inventory);
        showGUI();
        predatorUser.setPlayingMinigame(true);
        predatorUser.setCurrentMinigame(this);
    }

    public void stop() {
        reset();
        predatorUser.setPlayingMinigame(false);
        Player predator = predatorUser.getBukkitPlayer();
        if (predator != null) {
            predator.closeInventory();
        }
        victimUser.setPredator(null);
        predatorUser.setVictim(null);
        predatorUser.setCurrentMinigame(null);
    }

    private void reset() {
        correctClicks.set(0);
    }

    private void stealItem(Player thief, Player victim, ItemStack item) {
        victim.getInventory().setItem(victim.getInventory().first(item), null);
        Map<Integer, ItemStack> leftover = thief.getInventory().addItem(item);
        leftover.forEach((slot, stack) -> thief.getWorld().dropItemNaturally(thief.getLocation(), stack));
    }

    private double getPercentageOfVictimBalance(Player victim) {
        Economy economy = VaultHook.getEconomy();
        if (economy == null)
            return 0.0;
        return economy.getBalance(victim) * Config.getMoneyPercentageToSteal();
    }

    private void doMoneyTransaction(Player thief, Player victim, double amountStolen) {
        Economy economy = VaultHook.getEconomy();
        if (economy == null)
            return;
        if (amountStolen > 0) {
            economy.withdrawPlayer(victim, amountStolen);
            economy.depositPlayer(thief, amountStolen);
            thief.sendMessage(MessageConfig.getMoneyAmountReceivedMessage(String.format("%.2f", amountStolen)));
        } else {
            thief.sendMessage(MessageConfig.getNoMoneyReceivedMessage());
        }
    }

    private boolean isMoneyStealEnabled() {
        return VaultHook.isVaultEnabled() && Config.isMoneyCanBeStolen();
    }

    private void stealMoney() {
        if (isMoneyStealEnabled()) {
            double amountToSteal = getPercentageOfVictimBalance(victimUser.getBukkitPlayer());
            doMoneyTransaction(predatorUser.getBukkitPlayer(), victimUser.getBukkitPlayer(), amountToSteal);
        }
    }

    private void doPickpocketSuccess() {
        Player predator = predatorUser.getBukkitPlayer();
        stealItem(predator, victimUser.getBukkitPlayer(), item);
        stealMoney();
        playItemPickupSound(predator);
        predatorUser.setSteals(predatorUser.getSteals() + 1);
        predator.sendMessage(MessageConfig.getPickpocketSuccessfulMessage());
    }

    private void doPickpocketFailure() {
        Player predator = predatorUser.getBukkitPlayer();
        if (predator != null) {
            playBassSound(predator);
            predator.sendMessage(MessageConfig.getPickpocketUnsuccessfulMessage());
        }
        victimUser.playRummageSound();
    }

    private void doGameLoop() {
        shuffleInventoryItems();
        if (correctClicks.get() >= Config.getRequiredClicksToPickpocket()) {
            doPickpocketSuccess();
            stop();
        }
    }

    private void shuffleInventoryItems() {
        Inventory inventory = gui.getInventory();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (inventory.getItem(i) == null)
                continue;
            int randomSlot = (int) (Math.random() * INVENTORY_SIZE);
            ItemStack temp = inventory.getItem(randomSlot);
            if (temp == null)
                temp = new ItemStack(Material.AIR);
            inventory.setItem(randomSlot, inventory.getItem(i));
            inventory.setItem(i, temp);
        }
    }

    private Map<Integer, MenuItem> createMinigameMenuItems(Inventory inventory) {
        Map<Integer, MenuItem> menuItemMap = new HashMap<>();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null)
                stack = new ItemStack(Material.AIR, 1);
            MenuItem menuItem = new MenuItem(stack);
            menuItem.addListener(this::onMenuItemClick);
            menuItemMap.put(i, menuItem);
        }
        return menuItemMap;
    }

    private void onMenuItemClick(MenuItemClickEvent event) {
        if (event.getMenuItem() != null && event.getMenuItem().getItemStack().getType() == item.getType()) {
            correctClicks.incrementAndGet();
            playExperienceOrbPickupSound(event.getPlayer());
            playSoundForVictim(Sound.BLOCK_SNOW_STEP);
        } else {
            playBassSound(event.getPlayer());
            playSoundForVictim(Sound.BLOCK_SNOW_STEP);
        }
        doGameLoop();
    }

    private void playSoundForVictim(Sound sound) {
        Player victim = victimUser.getBukkitPlayer();
        if (victim != null) {
            victim.playSound(victim.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    private void playItemPickupSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }

    private void playExperienceOrbPickupSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private void playBassSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
    }
}
