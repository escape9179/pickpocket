package logan.pickpocket.listeners

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.user.PickpocketUser
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

        val user = PickpocketUser.get(event.player)
        PickpocketPlugin.database?.addUser(user)

        // return without showing status message
        if (!PickpocketPlugin.pickpocketConfiguration.statusOnLogin) return

        // show status message
        if (user.isParticipating)
            event.player.sendMessage(MessageConfiguration.participatingTrueNotificationMessage)
        else
            event.player.sendMessage(MessageConfiguration.participatingFalseNotificationMessage)
    }
}
