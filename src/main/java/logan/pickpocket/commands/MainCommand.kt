package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.util.sendColoredMessage
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

        sender.sendColoredMessage(
            """
            Pickpocket ${PickpocketPlugin.instance.name} ${PickpocketPlugin.pluginVersion}
            /pickpocket toggle - Toggle pick-pocketing for yourself.
            /pickpocket target - Pick-pocket a player near you.
            /pickpocket status - Check status of toggleable values.
            
            Admin commands:
            /pickpocket admin notify - Toggle admin notifications for yourself
            /pickpocket admin exempt [name] - Exempt yourself or another player from being stolen from
            /pickpocket admin bypass [name] - Toggle cooldown bypass for yourself or another player
    """.trimIndent()
        )
        return true
    }
}