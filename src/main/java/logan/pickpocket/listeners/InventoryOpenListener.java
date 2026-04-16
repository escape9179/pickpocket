package logan.pickpocket.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;

/**
 * Detects victim inventory opens while rummaging is active.
 */
public class InventoryOpenListener implements Listener {

    /**
     * Delegates inventory-open handling to the session manager.
     *
     * @param event inventory open event
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        PickpocketSessionManager.onInventoryOpened(PickpocketUser.get(player));
    }
}
