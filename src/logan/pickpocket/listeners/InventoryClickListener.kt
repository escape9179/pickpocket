package logan.pickpocket.listeners

import logan.api.gui.InventoryClickListener
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.Profiles
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

/**
 * Created by Tre on 12/28/2015.
 */
class InventoryClickListener : InventoryClickListener {
    override fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val profile = Profiles.get(player)
        val inventory: Inventory? = event.clickedInventory
        if (profile.isRummaging || profile.isPlayingMinigame) {
            event.isCancelled = true
            return
        }
        try {
            if (inventory?.getItem(event.slot) == null) {
                return
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        if (!profile.isPredator) {
            return
        }
        if (profile.victim!!.profileConfiguration.exemptSectionValue) {
            event.isCancelled = true
            profile.bukkitPlayer?.sendMessage(MessageConfiguration.personCantBeStolenFromMessage)
        }
    }
}