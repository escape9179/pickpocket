package logan.api.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        GUIAPI.callInventoryClickListeners(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GUIAPI.callInventoryCloseListeners(event);
    }
}
