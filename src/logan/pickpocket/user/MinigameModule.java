package logan.pickpocket.user;

import logan.config.MessageConfiguration;
import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.MenuItemClickEvent;
import logan.pickpocket.ColorUtils;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MinigameModule {
    public static final int MAX_TRIES = 5;
    public static final int INVENTORY_SIZE = 36;

    private PickpocketUser profile;
    private Player player;
    private PickpocketUser victim;
    private BukkitRunnable gameTimerRunnable;
    private BukkitTask gameTimerTask;
    private Menu minigameMenu;
    private AtomicInteger hitCount = new AtomicInteger(0);
    private AtomicInteger totalTries = new AtomicInteger(0);
    private AtomicBoolean hitInTime = new AtomicBoolean(false);
    private ItemStack clickedItem;

    public MinigameModule(PickpocketUser profile) {
        this.profile = profile;
        player = profile.getPlayer();
    }

    public void startMinigame(PickpocketUser victim, Inventory inventory, ItemStack clickedItem) {
        this.victim = victim;
        minigameMenu = new Menu("Pick-pocketing " + victim.getPlayer().getName(), INVENTORY_SIZE / 9);
        player.closeInventory();

        profile.setPlayingMinigame(true);
        this.clickedItem = clickedItem;

        Map<Integer, MenuItem> menuItemMap = createMinigameMenuItems(inventory);
        menuItemMap.forEach((k, v) -> minigameMenu.addItem(k, v));

        minigameMenu.show(player);

        resetGameTimerRunnable();
    }

    public void stopMinigame() {
        gameTimerTask.cancel();
        reset();
        profile.setPlayingMinigame(false);
        player.closeInventory();
    }

    private Map<Integer, MenuItem> createMinigameMenuItems(Inventory inventory) {
        Map<Integer, MenuItem> menuItemMap = new HashMap<>();

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack currentItem = inventory.getItem(i);
            MenuItem menuItem;

            if (currentItem == null) currentItem = new ItemStack(Material.AIR, 1);

            menuItem = new MenuItem(currentItem);
            menuItem.addListener(this::onMenuItemClick);

            menuItemMap.put(i, menuItem);
        }

        return menuItemMap;
    }

    private void onMenuItemClick(MenuItemClickEvent event) {
        final MenuItem clickedMenuItem = event.getMenuItem();

        gameTimerTask.cancel();

        // The user clicked the correct item
        if (clickedMenuItem != null && clickedMenuItem.getItemStack().getType().equals(clickedItem.getType())) {
            hitCount.incrementAndGet();
            hitInTime.set(true);

            // Play hit sound
            player.playSound(player.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundEntityExperienceOrbPickup(), 1.0f, 1.0f);
        } else {
            // Play miss sound
            player.playSound(player.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundBlockNoteBlockBass(), 1.0F, 1.0f);
        }

        doGameLoop();
    }

    private void reset() {
        hitCount.set(0);
        totalTries.set(0);
        hitInTime.set(false);
    }

    public BukkitRunnable scheduleNewShuffleRunnable() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!hitInTime.get()) {
                    doGameLoop();

                    // Play miss sound.
                    player.playSound(player.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundBlockNoteBlockBass(), 1.0F, 1.0f);
                }
            }
        };
    }

    public void doGameLoop() {
        shuffleInventoryItems();

        if (totalTries.incrementAndGet() >= MAX_TRIES) {
            // If the player got more right than wrong
            if (hitCount.get() >= MAX_TRIES) {
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_SUCCESSFUL_KEY));

                Inventory victimInventory = victim.getPlayer().getInventory();

                // Remove the item from victims inventory.
                victimInventory.setItem(victimInventory.first(clickedItem), null);

                // Add item to thieves inventory.
                player.getInventory().addItem(clickedItem);

                player.playSound(player.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundEntityItemPickup(), 1.0f, 1.0f);

                showAdminNotifications(true);
            }
            else {
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_UNSUCCESSFUL_KEY));

                // Play failure sound
                player.playSound(player.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundBlockNoteBlockBass(), 1.0f, 0.7f);

                showAdminNotifications(false);
            }

            stopMinigame();

            if (!profile.getProfileConfiguration().getBypassSectionValue())
                PickpocketPlugin.addCooldown(profile.getPlayer());
        } else {
            resetGameTimerRunnable();
            hitInTime.set(false);
        }
    }

    public void shuffleInventoryItems() {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack currentItem = minigameMenu.getInventory().getItem(i);
            if (currentItem == null) continue;
            int randomSlot = (int) (Math.random() * INVENTORY_SIZE);
            if (minigameMenu.getInventory().getItem(randomSlot) == null) {
                minigameMenu.getInventory().setItem(randomSlot, currentItem);
                minigameMenu.getInventory().setItem(i, null);
            } else {
                ItemStack temp = minigameMenu.getInventory().getItem(randomSlot);
                minigameMenu.getInventory().setItem(randomSlot, currentItem);
                minigameMenu.getInventory().setItem(i, temp);
            }
        }
    }

    public int getHitCount() {
        return hitCount.get();
    }

    public int getTotalTries() {
        return totalTries.get();
    }

    public Menu getMinigameMenu() {
        return minigameMenu;
    }

    public void resetGameTimerRunnable() {
        gameTimerRunnable = scheduleNewShuffleRunnable();
        gameTimerTask = gameTimerRunnable.runTaskLater(PickpocketPlugin.getInstance(), PickpocketPlugin.getPickpocketConfiguration().getMinigameRollRate());
    }

    public void showAdminNotifications(boolean success) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            PickpocketUser profile = Profiles.get(onlinePlayer);
            if (profile.getProfileConfiguration().getAdminSectionValue()) {
                onlinePlayer.sendMessage(ColorUtils.colorize("&7[Pickpocket] &f" + player.getName() + "&7 has " +
                        ((success) ? "&asucceeded" : "&cfailed") +
                        " &7in " + "pick-pocketing &f" + victim.getPlayer().getName() + "&7."));
            }
        });
    }
}
