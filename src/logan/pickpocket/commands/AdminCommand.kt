package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player

class AdminCommand : BasicCommand<Player>(
    "admin",
    argRange = 1..2,
    usage = """
        /pickpocket admin bypass [name]
        /pickpocket admin exempt [name]
        /pickpocket admin notify
    """.trimIndent(),
    argTypes = listOf(String::class, String::class),
    parentCommand = "pickpocket",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.admin"
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        PickpocketUser.get(sender).run {
            isAdmin = !isAdmin
            sender.sendMessage(MessageConfiguration.getAdminStatusChangeMessage(isAdmin))
            return false
        }
    }
}