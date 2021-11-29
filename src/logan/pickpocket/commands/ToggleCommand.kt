package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.Profiles
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

        val profile = Profiles.get(sender)
        profile.isParticipating = !profile.isParticipating
        sender.sendMessage(
            if (profile.isParticipating)
                MessageConfiguration.getPickpocketToggleOnMessage() else
                MessageConfiguration.getPickpocketToggleOffMessage()
        )

        return true
    }
}