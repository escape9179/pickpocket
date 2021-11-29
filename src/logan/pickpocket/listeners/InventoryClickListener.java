package logan.pickpocket.listeners;

import logan.api.gui.GUIAPI;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClickListener implements Listener {
    public InventoryClickListener() {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PickpocketUser profile = Profiles.get(player);
        Inventory inventory = event.getClickedInventory();

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
            event.setCancelled(true);
            profile.getBukkitPlayer().sendMessage(MessageConfiguration.getPersonCantBeStolenFromMessage());
        }
    }
}
