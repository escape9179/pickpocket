package logan.api.listener;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface InventoryCloseListener {
    void onInventoryClose(InventoryCloseEvent event);
}
