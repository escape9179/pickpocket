package logan.pickpocket.listeners

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.config.PickpocketConfiguration
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
        validateUserToggleStatus(user)

        if (PickpocketConfiguration.showStatusOnLogin)
            showStatusMessage(user)
    }

    private fun validateUserToggleStatus(user: PickpocketUser) {
        if (!PickpocketConfiguration.isParticipationTogglingEnabled) {
            user.isParticipating = true
        }
    }

    private fun showStatusMessage(user: PickpocketUser) {
        if (user.isParticipating)
            user.bukkitPlayer.sendMessage(MessageConfiguration.participatingTrueNotificationMessage)
        else
            user.bukkitPlayer.sendMessage(MessageConfiguration.participatingFalseNotificationMessage)
    }
}
