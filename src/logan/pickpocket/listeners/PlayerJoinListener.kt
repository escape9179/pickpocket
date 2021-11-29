package logan.pickpocket.listeners

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.Profiles
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Created by Tre on 12/28/2015.
 */
class PlayerJoinListener : Listener {
    init {
        PickpocketPlugin.registerListener(this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {

        val profile = Profiles.get(event.player)

        // return without showing status message
        if (!PickpocketPlugin.pickpocketConfiguration.isShowStatusOnLoginEnabled) return

        // show status message
        if (profile.isParticipating)
            event.player.sendMessage(MessageConfiguration.participatingTrueNotificationMessage)
        else
            event.player.sendMessage(MessageConfiguration.participatingFalseNotificationMessage)
    }
}
