package logan.pickpocket.events;

import logan.guiapi.GUIAPI;
import logan.pickpocket.main.PickpocketItem;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
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
public class InventoryClick implements Listener
{
    public InventoryClick()
    {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        GUIAPI.callInventoryClickEvents(event);

        Player    player      = (Player) event.getWhoClicked();
        Profile   profile     = Profiles.get(player);
        Inventory inventory   = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (profile.isPlayingMinigame())
        {
            event.setCancelled(true);
            return;
        }

        if (clickedItem == null)
        {
            return;
        }

        try
        {
            if (inventory.getItem(event.getSlot()) == null) return;
        }
        catch (NullPointerException e)
        {
            return;
        }

        if (!profile.isStealing()) return;
        if (Profiles.get(profile.getVictim()).getProfileConfiguration().getExemptSectionValue())
        {
            event.setCancelled(true);
            profile.getPlayer().sendMessage(ChatColor.GRAY + "This person cannot be stolen from.");
            return;
        }

        for (PickpocketItem pickpocketItem : PickpocketItem.values())
        {
            ItemStack pickpocketStack = pickpocketItem.getRawItemStack();

            if (clickedItem.getType() == pickpocketStack.getType() && clickedItem.getDurability() == pickpocketStack.getDurability())
            {
                // Cancel original click event
                event.setCancelled(true);

                profile.getMinigameModule().startMinigame(inventory, clickedItem);
                profile.setIsPlayingMinigame(true);

                return;
            }
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You cannot steal this item.");
    }
}
