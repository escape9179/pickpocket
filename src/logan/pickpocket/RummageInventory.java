package logan.pickpocket;

import logan.config.MessageConfiguration;
import logan.config.PickpocketConfiguration;
import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.fill.UniFill;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RummageInventory {

    private static final String menuTitle = "Rummage";
    private static final String rummageButtonText = "Keep rummaging...";
    private static final int randomItemCount = 4;
    private Menu menu;
    private Player predator;
    private Player victim;

    public RummageInventory(Player predator, Player victim) {
        this.predator = predator;
        this.victim = victim;

        menu = new Menu(menuTitle, 4);
        menu.fill(new UniFill(PickpocketPlugin.getAPIWrapper().getMaterialWhiteStainedGlassPane()));
        MenuItem rummageButton = new MenuItem(rummageButtonText, new ItemStack(Material.CHEST));
        rummageButton.addListener(clickEvent -> {
            populateRummageMenu();
            menu.addItem(menu.getBottomRight(), rummageButton);
            menu.update();

            // Perform probability of getting caught
            if (Math.random() < PickpocketConfiguration.getCaughtChance()) {
                victim.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_VICTIM_WARNING_KEY));

                // Close the rummage inventory
                menu.close();
                predator.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_NOTICED_WARNING_KEY));
            }

            // Play a sound when rummaging
            predator.playSound(predator.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundBlockSnowStep(), 1.0f, 0.5f);
        });
        populateRummageMenu();
        menu.addItem(menu.getBottomRight(), rummageButton);
        setVictim(victim);
    }

    public void show(Player player) {
        menu.show(player);
    }

    private void populateRummageMenu() {
        menu.clear();
        List<ItemStack> randomItems = getRandomItemsFromPlayer();
        menu.fill(new UniFill(PickpocketPlugin.getAPIWrapper().getMaterialWhiteStainedGlassPane()));
        for (ItemStack randomItem : randomItems) {
            int randomSlot = (int) (Math.random() * (menu.getSlots() - 9));
            MenuItem menuItem = new MenuItem(randomItem);
            menuItem.addListener(menuItemClickEvent -> {
                final ItemStack fillerItem = new ItemStack(PickpocketPlugin.getAPIWrapper().getMaterialWhiteStainedGlassPane());
                final int bottomRightSlot = menu.getBottomRight();
                menu.addItem(bottomRightSlot, new MenuItem(fillerItem));
                menu.update();
                menu.close();
                setRummaging(false);
                minigameModule.startMinigame(victim, menu.getInventory(), menuItemClickEvent.getInventoryClickEvent().getCurrentItem());
            });
            menu.addItem(randomSlot, menuItem);
        }
    }

    private List<ItemStack> getRandomItemsFromPlayer() {
        final List<ItemStack> randomItemList = new ArrayList<>();
        final ItemStack[] storageContents = PickpocketPlugin.getAPIWrapper().getInventoryStorageContents(victim.getInventory());
        final int inventorySize = PickpocketPlugin.getAPIWrapper().getInventoryStorageContents(victim.getInventory()).length;
        ItemStack randomItem;
        int randomSlot;

        outer:
        for (int i = 0; i < randomItemCount; i++) {
            randomSlot = 9 + (int) (Math.random() * (inventorySize - 9));
            randomItem = storageContents[randomSlot];

            if (randomItem == null) continue;

            // Check if the item is banned
            for (String disabledItem : PickpocketConfiguration.getDisabledItems()) {
                Material disabledItemType = Material.getMaterial(disabledItem.toUpperCase());

                // This item is disabled. Skip this random item iteration.
                if (randomItem.getType().equals(disabledItemType)) continue outer;
            }

            randomItemList.add(randomItem);
        }

        return randomItemList;
    }
}
