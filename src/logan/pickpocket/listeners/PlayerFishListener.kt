package logan.pickpocket.listeners

import logan.api.util.getRandomItemFromMainInventory
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

class PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        PickpocketPlugin.log("Entity caught is ${event.caught?.name}")
        lateinit var randomItem: ItemStack
        with (event.caught) {
            if (this == null || this !is Player) return
            randomItem = getRandomItemFromMainInventory() ?: return
            inventory.removeItem(randomItem)
        }
        with (event.player) {
            inventory.addItem(randomItem)
            sendMessage("You pickpocketed ${randomItem.i18NDisplayName} from ${event.caught?.name}.")
        }
    }
}