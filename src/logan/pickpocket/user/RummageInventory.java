package logan.pickpocket.user;

import logan.api.gui.Menu;
import logan.api.gui.MenuItem;
import logan.api.gui.fill.UniFill;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RummageInventory {

    private static final String menuTitle = "Rummage";
    private static final String rummageButtonText = "Keep rummaging...";
    private static final int randomItemCount = 4;
    private static final int rummageTimerTickRate = 20;
    private static final int ticksUntilNoticed = 4;
    int noticeTimerCurrentSlot = 0;
    final ItemStack noticeFillerItem;
    final ItemStack fillerItem;
    private BukkitTask rummageTimerTask;
    private MenuItem rummageButton;
    private Menu menu;
    private PickpocketUser victim;

    public RummageInventory(PickpocketUser victim) {
        this.victim = victim;

        fillerItem = new ItemStack(PickpocketPlugin.getAPIWrapper().getMaterialWhiteStainedGlassPane());
        noticeFillerItem = new ItemStack(PickpocketPlugin.getAPIWrapper().getItemStackRedStainedGlassPane());

        menu = new Menu(menuTitle, 4);
        menu.fill(new UniFill(fillerItem.getType()));
        rummageButton = new MenuItem(rummageButtonText, new ItemStack(Material.CHEST));
        rummageButton.addListener(clickEvent -> {
            PickpocketUser predator = victim.getPredator();
            populateRummageMenu();
            predator.playRummageSound();
        });
    }

    public void show(PickpocketUser predator) {
        predator.setVictim(victim);

        populateRummageMenu();
        menu.addItem(menu.getBottomRight(), rummageButton);
        menu.show(predator.getBukkitPlayer());

        // Start rummage timer
        rummageTimerTask = new BukkitRunnable() {
            AtomicInteger tickCount = new AtomicInteger(0);

            @Override
            public void run() {
                if (tickCount.getAndIncrement() >= ticksUntilNoticed) {
                    victim.playRummageSound();
                    // Close the rummage inventory
                    menu.close();
                    predator.sendMessage(MessageConfiguration.getPickpocketNoticedWarningMessage());
                    noticeTimerCurrentSlot = 0;
                    if (!predator.getProfileConfiguration().getBypassSectionValue())
                        PickpocketPlugin.addCooldown(predator.getBukkitPlayer());
                    rummageTimerTask.cancel();
                }
                int slotsToFill = menu.getSize() / ticksUntilNoticed;
                for (int i = noticeTimerCurrentSlot; i < slotsToFill + noticeTimerCurrentSlot; i++) {
                    if (menu.getInventory().getItem(i).getType() != fillerItem.getType())
                        continue;
                    menu.addItem(i, new MenuItem(noticeFillerItem));
                }
                noticeTimerCurrentSlot += slotsToFill;
                menu.update();
            }
        }.runTaskTimer(PickpocketPlugin.getInstance(), rummageTimerTickRate, rummageTimerTickRate);
    }

    private void populateRummageMenu() {
        menu.clear();
        List<ItemStack> randomItems = getRandomItemsFromPlayer();
        menu.fill(new UniFill(fillerItem.getType()));
        for (int i = 0; i < noticeTimerCurrentSlot; i++) {
            menu.addItem(i, new MenuItem(noticeFillerItem));
        }
        for (ItemStack randomItem : randomItems) {
            int randomSlot = (int) (Math.random() * (menu.getSize() - 9));
            MenuItem menuItem = new MenuItem(randomItem);
            menuItem.addListener(menuItemClickEvent -> {
                PickpocketUser predator = victim.getPredator();
                predator.setRummaging(false);

                rummageTimerTask.cancel();
                final int bottomRightSlot = menu.getBottomRight();
                menu.addItem(bottomRightSlot, new MenuItem(fillerItem));
                menu.update();
                menu.close();

                if (victim.getBukkitPlayer() == null || !victim.getBukkitPlayer().isOnline())
                    predator.sendMessage(ChatColor.RED + "Player is no longer available.");
                Minigame minigame = new Minigame(predator, victim, menuItemClickEvent.getInventoryClickEvent().getCurrentItem());
                minigame.start(menu.getInventory());
            });
            menu.addItem(randomSlot, menuItem);
        }
        menu.addItem(menu.getBottomRight(), rummageButton);
        menu.update();
    }

    private List<ItemStack> getRandomItemsFromPlayer() {
        final List<ItemStack> randomItemList = new ArrayList<>();
        final ItemStack[] storageContents = PickpocketPlugin.getAPIWrapper().getInventoryStorageContents(victim.getBukkitPlayer().getInventory());
        final int inventorySize = PickpocketPlugin.getAPIWrapper().getInventoryStorageContents(victim.getBukkitPlayer().getInventory()).length;
        ItemStack randomItem;
        int randomSlot;

        outer:
        for (int i = 0; i < randomItemCount; i++) {
            randomSlot = 9 + (int) (Math.random() * (inventorySize - 9));
            randomItem = storageContents[randomSlot];

            if (randomItem == null) continue;

            // Check if the item is banned
            for (String disabledItem : PickpocketPlugin.getPickpocketConfiguration().getDisabledItems()) {
                Material disabledItemType = Material.getMaterial(disabledItem.toUpperCase());

                // This item is disabled. Skip this random item iteration.
                if (randomItem.getType().equals(disabledItemType)) continue outer;
            }

            randomItemList.add(randomItem);
        }

        return randomItemList;
    }

    public void close() {
        rummageTimerTask.cancel();
    }
}
