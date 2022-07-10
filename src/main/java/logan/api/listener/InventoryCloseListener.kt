package logan.api.listener

import org.bukkit.event.inventory.InventoryCloseEvent

interface InventoryCloseListener {
    fun onInventoryClose(event: InventoryCloseEvent)
}