package logan.pickpocket.listeners;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Cancels inventory interaction while the thief is rummaging.
 */
public class InventoryClickListener implements logan.api.listener.InventoryClickListener {

    /**
     * Prevents normal item clicks when an active rummage UI is open.
     *
     * @param event inventory click event
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PickpocketUser user = PickpocketUser.get(player);
        Inventory inventory = event.getInventory();
        var pickSession = PickpocketSessionManager.getSession(user);
        if (pickSession != null && pickSession.isPickpocketing()) {
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
        if (pickSession == null || !pickSession.isThief(user)) {
            return;
        }
    }
}
