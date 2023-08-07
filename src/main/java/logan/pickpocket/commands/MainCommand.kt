package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.command.CommandSender

/* This is the command run when there are no arguments provided and
* the user is looking for more information about the plugin. */
class MainCommand : BasicCommand<CommandSender>(
    "pickpocket",
    target = SenderTarget.BOTH,
    permissionNode = "pickpocket.use",
    aliases = arrayOf("pp"),
    usage = """
        Usage:
        /pickpocket (no args)
    """.trimIndent()
) {

    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        sender.sendMessage(
            """
            Pickpocket ${PickpocketPlugin.instance.name} ${PickpocketPlugin.pluginVersion}
            Admin commands:
            /pickpocket admin notify - Toggle admin notifications for yourself
            /pickpocket admin exempt [name] - Exempt yourself or another player from being stolen from
            /pickpocket admin bypass [name] - Toggle cooldown bypass for yourself or another player
            /pickpocket admin toggle - Toggle pickpocketing on or off.
            /pickpocket admin status - View if pickpocketing is currently enabled or disabled.
    """.trimIndent()
        )
        return true
    }
}