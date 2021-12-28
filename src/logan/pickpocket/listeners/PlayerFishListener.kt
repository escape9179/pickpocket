package logan.pickpocket.listeners

import logan.api.util.getRandomItemFromMainInventory
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        if (!PickpocketPlugin.pickpocketConfiguration.isFishingRodEnabled) return
        val caught = event.caught as? Player ?: return
        val item = caught.getRandomItemFromMainInventory() ?: return
        event.player.run {
            inventory.addItem(item)
            sendMessage("You pickpocketed ${item.i18NDisplayName} from ${event.caught?.name}.")
        }
        caught.inventory.removeItem(item)
    }
}