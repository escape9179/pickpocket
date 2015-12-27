package logan.pickpocket.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tre on 12/26/2015.
 */
public class PickpocketItemInventory {

    public static final String NAME = "Pickpocket Items";

    private static ItemStack nextButton;
    private static ItemStack backButton;

    private static Player player;
    private static int inventoryOpen = 0;
    private static List<Inventory> inventoryList;

    public static void open(Profile profile) {
        player = profile.getPlayer();
        inventoryList = new ArrayList<>();
        List<PickpocketItem> profilePickpocketItems = profile.getPickpocketItems();
        List<PickpocketItem> pickpocketItems = Arrays.asList(PickpocketItem.values());

        if (!pickpocketItems.isEmpty()) inventoryList.add(Bukkit.createInventory(null, 54, "Pickpocket Items"));

        int slotNum = 0;
        int maxSize = 44;
        int inventoryNum = 1;
        Iterator<PickpocketItem> iterator = pickpocketItems.iterator();
        while (iterator.hasNext()) {
            if (slotNum > maxSize) {
                Inventory inventory = Bukkit.createInventory(null, 54, "Pickpocket Items Page: " + inventoryNum);
                inventoryList.add(addButtons(inventory));
                inventoryNum++;
                slotNum = 0;
            }
            slotNum++;
            iterator.next();
        }

        player.sendMessage("There were " + pickpocketItems.size() + " pickpocket items so " + inventoryList.size() + " inventories were created.");

        slotNum = 0;
        int itemNum = 0;

        itemPlacer:
        for (Inventory inventory : inventoryList) {
            for (int i = itemNum; i < pickpocketItems.size(); i++) {
                if (slotNum > maxSize) {
                    slotNum = 0;
                    continue itemPlacer;
                }

                PickpocketItem pickpocketItem = pickpocketItems.get(i);
                for (PickpocketItem profilePickpocketItem : profilePickpocketItems) {
                    if (profilePickpocketItem.getName().equals(pickpocketItem.getName())) {
                        inventory.setItem(slotNum, pickpocketItem.getItemStack(profile));
                    } else inventory.setItem(slotNum, pickpocketItem.getLockedItemStack(profile));
                }
                itemNum = i;
                slotNum++;
            }
        }

        player.openInventory(inventoryList.get(0));
    }

    public static String getNextButtonName() {
        return nextButton.getItemMeta().getDisplayName();
    }

    public static String getBackButtonName() {
        return backButton.getItemMeta().getDisplayName();
    }

    public static void nextPage() {
        player.openInventory(inventoryList.get(inventoryOpen++));
    }

    public static void previousPage() {
        player.openInventory(inventoryList.get(inventoryOpen--));
    }

    private static void configureButtons() {
        nextButton = new ItemStack(Material.PAPER);
        backButton = new ItemStack(Material.PAPER);

        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        nextButtonMeta.setDisplayName(ChatColor.GRAY + "Next");
        nextButton.setItemMeta(nextButtonMeta);

        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName(ChatColor.GRAY + "Back");
    }

    private static Inventory addButtons(Inventory inventory) {
        inventory.setItem(45, backButton);
        inventory.setItem(53, nextButton);
        return inventory;
    }

}
