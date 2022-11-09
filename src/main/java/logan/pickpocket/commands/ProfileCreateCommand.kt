package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.ProfileType
import logan.pickpocket.main.ThiefProfile
import logan.pickpocket.main.VictimProfile
import org.bukkit.command.CommandSender

class ProfileCreateCommand : BasicCommand<CommandSender>(
    "create",
    "pickpocket.profile.create",
    2..2,
    target = SenderTarget.BOTH,
    argTypes = listOf(String::class, String::class),
    parentCommand = "profile",
    usage = """
        Usage:
        /pickpocket profile create <thief|victim> <profile>
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        when (args[0].lowercase()) {
            ProfileType.THIEF.friendlyName -> {
                val result = ThiefProfile(args[1]).save()
                sender.sendMessage(
                    if (result) MessageConfiguration.getProfileThiefCreateSuccessMessage(args[1])
                    else MessageConfiguration.getProfileErrorAlreadyExistsMessage(args[1])
                )
            }
            ProfileType.VICTIM.friendlyName -> {
                val result = VictimProfile(args[1]).save()
                sender.sendMessage(
                    if (result) MessageConfiguration.getProfileVictimCreateSuccessMessage(args[1])
                    else MessageConfiguration.getProfileErrorAlreadyExistsMessage(args[1])
                )
            }
        }
        return true
    }
}
