package logan.pickpocket.listeners

import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        event.caught ?: run {
            PickpocketPlugin.log("No entity was caught.")
            return
        }
        PickpocketPlugin.log("Entity caught is ${event.caught?.name ?: "(null)"}")
    }
}