package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.command.CommandSender

class ProfileRemoveCommand : BasicCommand<CommandSender>(
    "remove",
    argRange = 2..2,
    argTypes = listOf(String::class, String::class),
    parentCommand = "profile",
    target = SenderTarget.BOTH,
    permissionNode = "pickpocket.profile.remove",
    usage = """
        Usage:
        /pickpocket profile remove <thief|victim> <profile>
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        val result = PickpocketPlugin.profileConfiguration.removeThiefProfile(args[1])
        if (!result) sender.sendMessage(MessageConfiguration.getProfileNotFoundMessage(args[1])).run { return false }
        sender.sendMessage(MessageConfiguration.getProfileRemovedMessage(args[1]))
        return true
    }
}