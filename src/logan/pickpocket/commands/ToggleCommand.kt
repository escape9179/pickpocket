package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player

class ToggleCommand : BasicCommand<Player>(
    name = "toggle",
    parentCommand = "pickpocket",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.toggle",
    usage = """
        Usage:
        /pickpocket toggle (no args)
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {

        val profile = PickpocketUser.get(sender)
        profile.isParticipating = !profile.isParticipating
        sender.sendMessage(
            if (profile.isParticipating)
                MessageConfiguration.pickpocketToggleOnMessage else
                MessageConfiguration.pickpocketToggleOffMessage
        )

        return true
    }
}