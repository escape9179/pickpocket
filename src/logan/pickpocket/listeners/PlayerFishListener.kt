package logan.pickpocket.listeners

import logan.api.util.getRandomItemFromMainInventory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        val caught = event.caught as? Player ?: return
        val item = caught.getRandomItemFromMainInventory() ?: return
        event.player.run {
            inventory.addItem(item)
            sendMessage("You pickpocketed ${item.i18NDisplayName} from ${event.caught?.name}.")
        }
        caught.inventory.removeItem(item)
    }
}