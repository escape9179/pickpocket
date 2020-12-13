package logan.pickpocket.listeners;

import logan.config.MessageConfiguration;
import logan.guiapi.GUIAPI;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClickListener implements Listener {
    public InventoryClickListener() {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        GUIAPI.callInventoryClickEvents(event);

        Player player = (Player) event.getWhoClicked();
        PickpocketUser profile = Profiles.get(player);
        Inventory inventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (profile.isRummaging() || profile.isPlayingMinigame()) {
            event.setCancelled(true);
            return;
        }

        try {
            if (inventory == null || inventory.getItem(event.getSlot()) == null) {
                return;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (!profile.isPredator()) {
            return;
        }

        if (profile.getVictim().getProfileConfiguration().getExemptSectionValue()) {
            System.out.println("Person can't be stolen from.");
            event.setCancelled(true);
            profile.getPlayer().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PERSON_CANT_BE_STOLEN_FROM_KEY));
        }
    }
}
