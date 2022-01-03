package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AdminBypassCommand : BasicCommand<Player>(
    "bypass",
    0..1,
    listOf(String::class),
    "admin",
    SenderTarget.PLAYER,
    "pickpocket.admin.bypass",
    """
        /pickpocket admin bypass - Enable cooldown delay bypass for yourself.
        /pickpocket admin bypass [name] - Enable cooldown delay bypass for another person.
    """.trimIndent(),
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {

        if (args.isEmpty()) {
            val profile = PickpocketUser.get(sender)
            val bypassStatus = !profile.playerConfiguration.bypassSectionValue
            profile.playerConfiguration.setBypassSection(bypassStatus)
            sender.sendMessage(MessageConfiguration.getBypassStatusChangeMessage(bypassStatus))
        } else {
            val otherPlayer = Bukkit.getPlayer(args[0]) ?: run {
                sender.sendMessage(MessageConfiguration.playerNotFoundMessage)
                return true
            }
            val otherPlayerProfile = PickpocketUser.get(otherPlayer)
            val bypassStatus = !otherPlayerProfile.playerConfiguration.bypassSectionValue
            otherPlayerProfile.playerConfiguration.setBypassSection(bypassStatus)
            sender.sendMessage(MessageConfiguration.getBypassStatusChangeOtherMessage(otherPlayer, bypassStatus))
            otherPlayer.sendMessage(MessageConfiguration.getBypassStatusChangeMessage(bypassStatus))
        }
        return true
    }
}