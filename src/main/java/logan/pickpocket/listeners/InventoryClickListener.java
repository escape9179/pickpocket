package logan.pickpocket.listeners;

import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClickListener implements logan.api.listener.InventoryClickListener {

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PickpocketUser profile = PickpocketUser.get(player);
        Inventory inventory = event.getInventory();
        if (profile.isRummaging() || profile.isPlayingMinigame()) {
            event.setCancelled(true);
            return;
        }
        try {
            if (inventory.getItem(event.getSlot()) == null) {
                return;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (!profile.isPredator()) {
            return;
        }
        if (profile.getVictim().isExempt()) {
            event.setCancelled(true);
            Player bukkitPlayer = profile.getBukkitPlayer();
            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(MessageConfiguration.getPersonCantBeStolenFromMessage());
            }
        }
    }
}
