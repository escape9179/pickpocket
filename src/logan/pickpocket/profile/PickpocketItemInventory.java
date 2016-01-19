package logan.pickpocket.profile;

import logan.pickpocket.main.PickpocketItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by Tre on 12/26/2015.
 */
public class PickpocketItemInventory {

    public static final String NAME = "Pickpocket Items";

    private ItemStack nextButton;
    private ItemStack backButton;

    private int inventoryOpen = 0;
    private List<Inventory> inventoryList;

    private Profile profile;
    private Player player;

    public PickpocketItemInventory(Profile profile) {
        this.profile = profile;
    }

    public void open() {
        player = profile.getPlayer();
        inventoryList = new ArrayList<>();
        Set<PickpocketItem> profilePickpocketItems = profile.getPickpocketItemModule().getPickpocketItemIntegerMap().keySet();
        List<PickpocketItem> pickpocketItems = Arrays.asList(PickpocketItem.values());
        inventoryOpen = 0;

        configureButtons();

        if (!pickpocketItems.isEmpty()) inventoryList.add(Bukkit.createInventory(null, 54, "Pickpocket Items Page: 1"));

        int slotNum = 0;
        int maxSize = 44;
        int inventoryNum = 2;
        Iterator<PickpocketItem> iterator = pickpocketItems.iterator();
        while (iterator.hasNext()) {
            if (slotNum > maxSize) {
                Inventory inventory = Bukkit.createInventory(null, 54, "Pickpocket Items Page: " + inventoryNum);
                inventoryList.add(inventory);
                inventoryNum++;
                slotNum = 0;
            }
            slotNum++;
            iterator.next();
        }

        inventoryNum = 0;
        slotNum = 0;
        itemPlacer:
        for (PickpocketItem pickpocketItem : pickpocketItems) {
            if (slotNum > maxSize) {
                inventoryNum++;
                slotNum = 0;
            }
            Inventory inventory = inventoryList.get(inventoryNum);
            for (PickpocketItem profileItem : profilePickpocketItems) {
                if (profileItem.getRawName().equals(pickpocketItem.getRawName())) {
                    inventory.setItem(slotNum, profileItem.getItemStack(profile));
                    slotNum++;
                    continue itemPlacer;
                }
            }
            inventory.setItem(slotNum, pickpocketItem.getLockedItemStack(profile));

            slotNum++;
        }

        player.openInventory(addButtons(inventoryList.get(inventoryOpen)));
    }

    public String getNextButtonName() {
        return nextButton.getItemMeta().getDisplayName();
    }

    public String getBackButtonName() {
        return backButton.getItemMeta().getDisplayName();
    }

    public void nextPage() {
        if (inventoryOpen == inventoryList.size() - 1) return;
        player.openInventory(addButtons(inventoryList.get(++inventoryOpen)));
    }

    public void previousPage() {
        if (inventoryOpen == 0) return;
        player.openInventory(addButtons(inventoryList.get(--inventoryOpen)));
    }

    private void configureButtons() {
        nextButton = new ItemStack(Material.PAPER);
        backButton = new ItemStack(Material.PAPER);

        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        nextButtonMeta.setDisplayName(ChatColor.GRAY + "Next");
        nextButton.setItemMeta(nextButtonMeta);

        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName(ChatColor.GRAY + "Back");
        backButton.setItemMeta(backButtonMeta);
    }

    private Inventory addButtons(Inventory inventory) {
        inventory.setItem(45, backButton);
        inventory.setItem(53, nextButton);
        return inventory;
    }

}
