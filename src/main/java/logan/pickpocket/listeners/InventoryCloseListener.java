package logan.pickpocket.listeners;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Handles cleanup when rummage inventories are closed.
 */
public class InventoryCloseListener implements logan.api.listener.InventoryCloseListener {

    /**
     * Delegates close handling to the session manager.
     *
     * @param event inventory close event
     */
    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PickpocketUser user = PickpocketUser.get(player);
        PickpocketSessionManager.onInventoryClosed(user);
    }
}
