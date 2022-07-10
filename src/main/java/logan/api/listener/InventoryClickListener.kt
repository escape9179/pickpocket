package logan.api.listener

import org.bukkit.event.inventory.InventoryClickEvent

interface InventoryClickListener {
    fun onInventoryClick(event: InventoryClickEvent)
}