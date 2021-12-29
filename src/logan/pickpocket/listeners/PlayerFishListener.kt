package logan.pickpocket.listeners

import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        if (!PickpocketPlugin.pickpocketConfiguration.isFishingRodEnabled) return
        val caught = event.caught as? Player ?: return
        PickpocketUser.get(event.player).doPickpocket(PickpocketUser.get(caught))
    }
}