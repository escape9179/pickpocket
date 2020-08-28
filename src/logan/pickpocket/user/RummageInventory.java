package logan.pickpocket.user;

import logan.config.MessageConfiguration;
import logan.config.PickpocketConfiguration;
import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.fill.UniFill;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RummageInventory {

    private static final String menuTitle = "Rummage";
    private static final String rummageButtonText = "Keep rummaging...";
    private MenuItem rummageButton;
    private static final int randomItemCount = 4;
    private Menu menu;
    private PickpocketUser victim;

    public RummageInventory(PickpocketUser victim) {
        this.victim = victim;

        menu = new Menu(menuTitle, 4);
        menu.fill(new UniFill(PickpocketPlugin.getAPIWrapper().getMaterialWhiteStainedGlassPane()));
        rummageButton = new MenuItem(rummageButtonText, new ItemStack(Material.CHEST));
        rummageButton.addListener(clickEvent -> {
            PickpocketUser predator = victim.getPredator();
            populateRummageMenu();

            // Perform probability of getting caught
            if (Math.random() < PickpocketConfiguration.getCaughtChance()) {
                victim.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_VICTIM_WARNING_KEY));

                // Close the rummage inventory
                menu.close();
                predator.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_NOTICED_WARNING_KEY));
            }

            predator.playRummageSound();
        });
    }

    public void show(PickpocketUser predator) {
        predator.setVictim(victim);

        populateRummageMenu();
        menu.addItem(menu.getBottomRight(), rummageButton);
        menu.show(predator.getPlayer());
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

                PickpocketUser predator = victim.getPredator();
                predator.setRummaging(false);
                predator.getMinigameModule().startMinigame(victim, menu.getInventory(), menuItemClickEvent.getInventoryClickEvent().getCurrentItem());
            });
            menu.addItem(randomSlot, menuItem);
            menu.addItem(menu.getBottomRight(), rummageButton);
            menu.update();
        }
    }

    private List<ItemStack> getRandomItemsFromPlayer() {
        final List<ItemStack> randomItemList = new ArrayList<>();
        final ItemStack[] storageContents = PickpocketPlugin.getAPIWrapper().getInventoryStorageContents(victim.getPlayer().getInventory());
        final int inventorySize = PickpocketPlugin.getAPIWrapper().getInventoryStorageContents(victim.getPlayer().getInventory()).length;
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
