package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.config.Config
import org.bukkit.entity.Player

class ToggleCommand : BasicCommand<Player>(
    name = "toggle",
    parentCommand = "admin",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.admin.toggle",
    usage = """
        Usage:
        /pickpocket admin toggle (no args)
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val status = !Config.isPickpocketingEnabled
        Config.isPickpocketingEnabled = status
        sender.sendMessage(
            if (status)
                MessageConfiguration.pickpocketToggleOnMessage else
                MessageConfiguration.pickpocketToggleOffMessage
        )
        return true
    }
}