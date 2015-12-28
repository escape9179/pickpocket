package logan.pickpocket.events;

import logan.pickpocket.main.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClick {

    private PickPocket pickPocket;

    public InventoryClick(PickPocket pickPocket) {
        this.pickPocket = pickPocket;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null
                || currentItem.getItemMeta() == null
                || currentItem.getItemMeta().getDisplayName() == null) {
            event.setCancelled(true);
            return;
        }
        if (inventory.getName().contains(PickpocketItemInventory.NAME)) {
            if (currentItem.getItemMeta().getDisplayName().equals(PickpocketItemInventory.getNextButtonName())) {
                PickpocketItemInventory.nextPage();
            }
            if (currentItem.getItemMeta().getDisplayName().equals(PickpocketItemInventory.getBackButtonName())) {
                PickpocketItemInventory.previousPage();
            }
            event.setCancelled(true);
        } else {
            Player player = (Player) event.getWhoClicked();
            Profile profile = ProfileHelper.getLoadedProfile(player, pickPocket.getProfiles());
            if (!profile.isStealing()) return;

            for (PickpocketItem pickpocketItem : PickpocketItem.values()) {
                if (currentItem.getType().equals(pickpocketItem.getMaterial())) {
                    event.setCancelled(!testChance(profile, pickpocketItem));
                    return;
                }
            }

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "This item hasn't been added to the plugin yet!");
        }
    }

    public boolean testChance(Profile profile, PickpocketItem pickpocketItem) {
        if (Math.random() < pickpocketItem.calculateExperienceBasedChance(profile.getExperience())) {
            profile.giveExperience(pickpocketItem.getExperienceValue());
            if (profile.givePickpocketItem(pickpocketItem) == false) {
                pickPocket.getServer().broadcastMessage(ChatColor.GRAY + profile.getPlayer().getName() + ChatColor.WHITE + " recieved the pickpocket item " + pickpocketItem.getName() + " (" + pickpocketItem.getExperienceValue() + "XP)!");
            }
        } else {
            profile.getPlayer().sendMessage(ChatColor.RED + "Theft unsuccessful.");
            profile.getVictim().sendMessage(ChatColor.GRAY + profile.getPlayer().getName() + ChatColor.RED + " has attempted to steal from you.");
            profile.setStealing(null);
            return false;
        }

        return true;
    }
}
