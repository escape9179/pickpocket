package logan.pickpocket.listeners;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        if (event.getClickedInventory() == null || event.getSlot() < 0) {
            return;
        }
        var pickSession = PickpocketSessionManager.getSession(user);
        if (pickSession != null && pickSession.isPickpocketing()) {
            event.setCancelled(true);
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        if (pickSession == null || !pickSession.isThief(user)) {
            return;
        }
    }
}
