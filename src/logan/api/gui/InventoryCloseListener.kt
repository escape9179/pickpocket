package logan.api.gui

import org.bukkit.event.inventory.InventoryCloseEvent

interface InventoryCloseListener {
    fun onInventoryClose(event: InventoryCloseEvent)
}