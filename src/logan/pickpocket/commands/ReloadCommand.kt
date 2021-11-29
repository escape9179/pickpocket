package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.command.CommandSender

class ReloadCommand : BasicCommand<CommandSender>(
    name = "reload",
    parentCommand = "pickpocket",
    target = SenderTarget.BOTH,
    permissionNode = "pickpocket.reload",
    usage = """
        Usage:
        /pickpocket reload (no args)
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        PickpocketPlugin.pickpocketConfiguration.reload()
        MessageConfiguration.reload()
        sender.sendMessage(MessageConfiguration.reloadNotificationMessage)
        return true
    }
}