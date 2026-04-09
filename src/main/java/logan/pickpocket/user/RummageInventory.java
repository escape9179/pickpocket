package logan.pickpocket.user;

import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.api.util.PlayerUtils;
import logan.pickpocket.main.PickpocketUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RummageInventory {

    private static final String MENU_TITLE = "Rummage";
    private static final String RUMMAGE_BUTTON_TEXT = "Keep rummaging...";

    private final PickpocketUser victim;
    private final PlayerInventoryMenu menu;
    private final MenuItem rummageButton;

    public RummageInventory(PickpocketUser victim) {
        this.victim = victim;
        this.menu = new PlayerInventoryMenu(MENU_TITLE, 4);
        this.rummageButton = new MenuItem(RUMMAGE_BUTTON_TEXT, new ItemStack(Material.CHEST));
        rummageButton.addListener(event -> {
            PickpocketUser predator = victim.getPredator();
            populateRummageMenu();
            predator.playRummageSound();
            victim.playRummageSound();
        });
    }

    public void show(PickpocketUser predator) {
        predator.setVictim(victim);
        populateRummageMenu();
        menu.addItem(menu.getBottomRight(), rummageButton);
        menu.show(predator.getBukkitPlayer());
    }

    private void populateRummageMenu() {
        menu.clear();
        List<ItemStack> randomItems = getRandomItemsFromPlayer();
        for (ItemStack randomItem : randomItems) {
            int randomSlot = (int) (Math.random() * (menu.getSize() - 9));
            MenuItem menuItem = new MenuItem(randomItem);
            menuItem.addListener(menuItemClickEvent -> {
                PickpocketUser predator = victim.getPredator();
                predator.setRummaging(false);
                int bottomRightSlot = menu.getBottomRight();
                menu.addItem(bottomRightSlot, new MenuItem(new ItemStack(Material.AIR)));
                menu.update();
                menu.close();
                if (victim.getBukkitPlayer() == null || !victim.getBukkitPlayer().isOnline()) {
                    predator.sendMessage(ChatColor.RED + "Player is no longer available.");
                }
                Minigame minigame = new Minigame(
                        predator, victim,
                        menuItemClickEvent.getInventoryClickEvent().getCurrentItem());
                minigame.start(menu.getInventory());
            });
            menu.addItem(randomSlot, menuItem);
        }
        menu.addItem(menu.getBottomRight(), rummageButton);
        menu.update();
    }

    private List<ItemStack> getRandomItemsFromPlayer() {
        List<ItemStack> randomItemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) { // TODO: Add config option for max items to rummage
            ItemStack randomItem = PlayerUtils.getRandomItemFromMainInventory(victim.getBukkitPlayer());
            if (randomItem == null)
                continue;
            if (PickpocketUtils.isItemTypeDisabled(randomItem.getType()))
                continue;
            randomItemList.add(randomItem);
        }
        return randomItemList;
    }
}
