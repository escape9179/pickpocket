package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.Config
import org.bukkit.entity.Player

class StatusCommand : BasicCommand<Player>(
    "status",
    parentCommand = "admin",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.admin.status",
    usage = """
        Usage:
        /pickpocket admin status (no args)"
    """.trimIndent()
) {

    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        sender.sendMessage("Pickpocketing is ${Config.isPickpocketingEnabled}.")
        return true
    }
}