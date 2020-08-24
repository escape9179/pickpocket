package logan.pickpocket.listeners;

import logan.config.MessageConfiguration;
import logan.guiapi.GUIAPI;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
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

        if (clickedItem == null || profile.isRummaging() || profile.isPlayingMinigame())
        {
            event.setCancelled(true);
            return;
        }

        try
        {
            if (inventory.getItem(event.getSlot()) == null)
            {
                return;
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        if (!profile.isPredator()) {
            return;
        }

        if (Profiles.get(profile.getVictim()).getProfileConfiguration().getExemptSectionValue())
        {
            event.setCancelled(true);
            profile.getPlayer().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PERSON_CANT_BE_STOLEN_FROM_KEY));
        }
    }
}
