package logan.pickpocket.listeners;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryCloseListener implements logan.api.listener.InventoryCloseListener {

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PickpocketUser user = PickpocketUser.get(player);
        PickpocketSessionManager.onInventoryClosed(user);
    }
}
