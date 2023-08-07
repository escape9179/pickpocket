package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.Profile
import org.bukkit.command.CommandSender

class ProfileCreateCommand : BasicCommand<CommandSender>(
    "create",
    "pickpocket.profile.create",
    1..1,
    target = SenderTarget.BOTH,
    argTypes = listOf(String::class, String::class),
    parentCommand = "profile",
    usage = """
        Usage:
        /pickpocket profile create <profile>
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        val result = Profile(args[0]).save()
        sender.sendMessage(
            if (result) MessageConfiguration.getProfileCreateSuccessMessage(args[0])
            else MessageConfiguration.getProfileErrorAlreadyExistsMessage(args[0])
        )
        return true
    }
}
