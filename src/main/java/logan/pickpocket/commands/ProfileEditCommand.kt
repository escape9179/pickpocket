package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.command.CommandSender

class ProfileEditCommand : BasicCommand<CommandSender>(
    "edit",
    argRange = 3..3,
    argTypes = listOf(String::class, String::class, String::class, String::class),
    parentCommand = "profile",
    target = SenderTarget.BOTH,
    permissionNode = "pickpocket.profile.edit",
    usage = """
        Usage:
        /pickpocket profile edit <profile> <property> <value>
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        val profile = PickpocketPlugin.profileConfiguration.loadProfile(args[0]) ?: run {
            sender.sendMessage(MessageConfiguration.getProfileNotFoundMessage(args[0]))
            return true
        }
        if (profile.properties.containsKey(args[1])) {
            val previousValue = profile.properties[args[1]]
            profile.properties[args[1]] = args[2]
            sender.sendMessage(
                MessageConfiguration.getProfileChangePropertyMessage(
                    args[1],
                    previousValue.toString(),
                    args[2],
                    profile.name
                )
            )
            profile.save()
        }
        return true
    }
}