package logan.pickpocket.profile;

import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.MenuItemClickEvent;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MinigameModule
{

    public static final long MAX_TIME       = 20 * 1;
    public static final int  MAX_TRIES      = 5;
    public static final int  INVENTORY_SIZE = 36;

    private Profile        profile;
    private Player         player;
    private BukkitRunnable gameTimerRunnable;
    private BukkitTask     gameTimerTask;
    private Menu           minigameMenu = new Menu("", INVENTORY_SIZE / 9);
    private AtomicInteger  hitCount     = new AtomicInteger(0);
    private AtomicInteger  totalTries   = new AtomicInteger(0);
    private AtomicBoolean  hitInTime    = new AtomicBoolean(false);
    private ItemStack      clickedItem;

    public MinigameModule(Profile profile)
    {
        this.profile = profile;
        player       = profile.getPlayer();
    }

    public void startMinigame(Inventory inventory, ItemStack clickedItem)
    {
        this.clickedItem = clickedItem;

        player.closeInventory();

        Map<Integer, MenuItem> menuItemMap = createMinigameMenuItems(inventory);
        menuItemMap.forEach((k, v) -> minigameMenu.addItem(k, v));

        minigameMenu.show(player);

        resetGameTimerRunnable();
    }

    public void stopMinigame()
    {
        gameTimerTask.cancel();
        reset();
        profile.setIsPlayingMinigame(false);
        player.closeInventory();
    }

    private Map<Integer, MenuItem> createMinigameMenuItems(Inventory inventory)
    {
        Map<Integer, MenuItem> menuItemMap = new HashMap<>();

        for (int i = 0; i < INVENTORY_SIZE; i++)
        {
            ItemStack currentItem = inventory.getItem(i);
            MenuItem  menuItem;

            if (currentItem == null) currentItem = new ItemStack(Material.AIR, 1);

            menuItem = new MenuItem(currentItem);
            menuItem.addListener(this::onMenuItemClick);

            menuItemMap.put(i, menuItem);
        }

        return menuItemMap;
    }

    private void onMenuItemClick(MenuItemClickEvent event)
    {
        final MenuItem clickedMenuItem = event.getMenuItem();

        gameTimerTask.cancel();

        // The user clicked the correct item
        if (clickedMenuItem != null && clickedMenuItem.getItemStack().getType().equals(clickedItem.getType()))
        {
            hitCount.incrementAndGet();
            hitInTime.set(true);

            // Play hit sound
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
        else
        {
            // Play miss sound
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0f);
        }

        // Add the item to the players inventory upon winning mini-game.
        boolean success = doGameLoop();
        if (success)
        {
            Player    victim          = Profiles.get(player).getVictim();
            Inventory victimInventory = victim.getInventory();

            // Remove the item from victims inventory.
            victimInventory.setItem(victimInventory.first(clickedItem), null);

            // Add item to thieves inventory.
            player.getInventory().addItem(clickedItem);
        }
    }

    private void reset()
    {
        hitCount.set(0);
        totalTries.set(0);
        hitInTime.set(false);
    }

    public BukkitRunnable scheduleNewShuffleRunnable()
    {
        return new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!hitInTime.get())
                {
                    doGameLoop();

                    // Play miss sound.
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0f);
                }
            }
        };
    }

    public boolean doGameLoop()
    {
        boolean success = false;

        shuffleInventoryItems();

        if (totalTries.incrementAndGet() >= MAX_TRIES)
        {
            // If the player got more right than wrong
            if (hitCount.get() > MAX_TRIES / 2)
            {
                player.sendMessage(ChatColor.GREEN + "Theft successful.");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);

                success = true;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Theft unsuccessful.");

                // Play failure sound
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.7f);
            }

            stopMinigame();

            if (!profile.getProfileConfiguration().getBypassSectionValue())
                PickpocketPlugin.addCooldown(profile.getPlayer());
        }
        else
        {
            resetGameTimerRunnable();
            hitInTime.set(false);
            success = false;
        }

        return success;
    }

    public void shuffleInventoryItems()
    {
        for (int i = 0; i < INVENTORY_SIZE; i++)
        {
            ItemStack currentItem = minigameMenu.getInventory().getItem(i);
            if (currentItem == null) continue;
            int randomSlot = (int) (Math.random() * INVENTORY_SIZE);
            if (minigameMenu.getInventory().getItem(randomSlot) == null)
            {
                minigameMenu.getInventory().setItem(randomSlot, currentItem);
                minigameMenu.getInventory().setItem(i, null);
            }
            else
            {
                ItemStack temp = minigameMenu.getInventory().getItem(randomSlot);
                minigameMenu.getInventory().setItem(randomSlot, currentItem);
                minigameMenu.getInventory().setItem(i, temp);
            }
        }
    }

    public int getHitCount()
    {
        return hitCount.get();
    }

    public int getTotalTries()
    {
        return totalTries.get();
    }

    public Menu getMinigameMenu()
    {
        return minigameMenu;
    }

    public void resetGameTimerRunnable()
    {
        gameTimerRunnable = scheduleNewShuffleRunnable();
        gameTimerTask     = gameTimerRunnable.runTaskLater(PickpocketPlugin.getInstance(), MAX_TIME);
    }
}
