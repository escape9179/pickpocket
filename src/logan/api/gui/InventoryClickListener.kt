package logan.api.gui

import org.bukkit.event.inventory.InventoryClickEvent

interface InventoryClickListener {
    fun onInventoryClick(event: InventoryClickEvent)
}