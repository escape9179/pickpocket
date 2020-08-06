package logan.pickpocket.profile;

import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.MenuItemClickEvent;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.ChatColor;
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

public class MinigameModule
{

    public static final long MAX_TIME       = 20 * 1;
    public static final int  MAX_TRIES      = 5;
    public static final int  INVENTORY_SIZE = 36;

    private Profile        profile;
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
    }

    public void startMinigame(Inventory inventory, ItemStack clickedItem)
    {
        this.clickedItem = clickedItem;
        Player player = profile.getPlayer();


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
        minigameMenu.close();
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
        final Player   player          = event.getPlayer();

        gameTimerTask.cancel();

        // The user clicked the correct item
        if (clickedMenuItem != null && clickedMenuItem.getItemStack().getType().equals(clickedItem.getType()))
        {
            hitCount.incrementAndGet();
            hitInTime.set(true);
            player.sendMessage(ChatColor.GREEN + "Hit " + profile.getMinigameModule().getHitCount() + " out of " + MAX_TRIES + ".");
        }
        else
        {
            player.sendMessage(ChatColor.RED + "Miss.");
        }

        doGameLoop();
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
                System.out.println("Hit: " + hitInTime.get() + ".");

                if (!hitInTime.get())
                {
                    profile.getPlayer().sendMessage(ChatColor.RED + "Miss!");
                    doGameLoop();
                }
            }
        };
    }

    public void doGameLoop()
    {
        shuffleInventoryItems();

        if (totalTries.incrementAndGet() >= MAX_TRIES)
        {
            // If the player got more right than wrong
            if (hitCount.get() > MAX_TRIES / 2)
            {
                profile.getPlayer().sendMessage(ChatColor.GREEN + "Pickpocket success.");
            }
            else
            {
                profile.getPlayer().sendMessage(ChatColor.RED + "Pickpocket failure.");
            }

            gameTimerTask.cancel();
            profile.setIsPlayingMinigame(false);
            minigameMenu.close();
            reset();
            profile.getPlayer().sendMessage("Mini-game over.");

            if (!profile.getProfileConfiguration().getBypassSectionValue())
                PickpocketPlugin.addCooldown(profile.getPlayer());
        }
        else
        {
            resetGameTimerRunnable();
            hitInTime.set(false);
            System.out.println("Total tries: " + profile.getMinigameModule().getTotalTries() + ".");
        }
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
