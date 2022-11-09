package logan.pickpocket.listeners

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import logan.api.listener.InventoryClickListener as ApiInventoryClickListener

/**
 * Created by Tre on 12/28/2015.
 */
class InventoryClickListener : ApiInventoryClickListener {
    override fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val profile = PickpocketUser.get(player)
//        val inventory: Inventory? = event.clickedInventory
        val inventory: Inventory? = event.inventory
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
        if (profile.victim!!.isExempt) {
            event.isCancelled = true
            profile.bukkitPlayer?.sendMessage(MessageConfiguration.personCantBeStolenFromMessage)
        }
    }
}