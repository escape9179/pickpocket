package logan.pickpocket.listeners

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.Profiles
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import logan.api.gui.InventoryCloseListener as ApiInventoryCloseListener

/**
 * Created by Tre on 12/28/2015.
 */
class InventoryCloseListener : ApiInventoryCloseListener {
    override fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val profile = Profiles.get(player)
        if (profile.isPlayingMinigame) {
            profile.currentMinigame!!.stop()
            player.sendMessage(MessageConfiguration.pickpocketUnsuccessfulMessage)
        }
        if (profile.isRummaging) {
            profile.openRummageInventory!!.close()
            profile.isRummaging = false
            profile.victim!!.predator = null
            profile.victim = null
        }
    }
}