package logan.pickpocket.events;

import logan.pickpocket.main.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClick implements Listener {

    private PickPocket pickPocket;

    public InventoryClick(PickPocket pickPocket) {
        this.pickPocket = pickPocket;
        pickPocket.getServer().getPluginManager().registerEvents(this, pickPocket);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = ProfileHelper.getLoadedProfile(player, pickPocket.getProfiles());
        PickpocketItemInventory pickpocketItemInventory = profile.getPickpocketItemInventory();
        Inventory inventory = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();
        try {
            if (inventory.getItem(event.getSlot()) == null) return;
        } catch (NullPointerException e) {
            return;
        }
        if (inventory.getName().contains(PickpocketItemInventory.NAME)) {
            if (currentItem.getItemMeta().getDisplayName().equals(pickpocketItemInventory.getNextButtonName()))
                pickpocketItemInventory.nextPage();
            if (currentItem.getItemMeta().getDisplayName().equals(pickpocketItemInventory.getBackButtonName()))
                pickpocketItemInventory.previousPage();
            event.setCancelled(true);
        } else {
            if (!profile.isStealing()) return;
            if (profile.getVictim().hasPermission(pickPocket.pickpocketExemept)) {
                event.setCancelled(true);
                profile.getPlayer().sendMessage(ChatColor.GRAY + "This person cannot  be stolen from.");
                return;
            }


            for (PickpocketItem pickpocketItem : PickpocketItem.values()) {
                if (currentItem.getType().equals(pickpocketItem.getMaterial())) {
                    boolean shouldCancel = !testChance(profile, pickpocketItem);
                    event.setCancelled(shouldCancel);
                    if (!event.isCancelled()) {
                        player.getInventory().addItem(currentItem);
                        inventory.remove(currentItem);
                    } else event.setCancelled(true);
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
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp() || player.hasPermission(pickPocket.pickpocketAdmin)) {
                    player.sendMessage(profile.getPlayer().getName() + " attempted to steal from " + profile.getVictim().getName() + ".");
                }
            }
            profile.setStealing(null);
            return false;
        }

        return true;
    }
}
