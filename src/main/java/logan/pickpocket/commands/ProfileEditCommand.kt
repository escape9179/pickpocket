package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.ProfileType
import org.bukkit.command.CommandSender

class ProfileEditCommand : BasicCommand<CommandSender>(
    "edit",
    4..4,
    listOf(String::class, String::class, String::class, String::class),
    "profile",
    SenderTarget.BOTH,
    "pickpocket.profile.edit",
    """
        Usage:
        /pickpocket profile edit <thief|victim> <profile> <property> <value>
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        when (args[0]) {
            ProfileType.THIEF.friendlyName -> {
                val profile = PickpocketPlugin.profileConfiguration.loadThiefProfile(args[1]) ?: run {
                    sender.sendMessage(MessageConfiguration.getProfileNotFoundMessage(args[1]))
                    return true
                }
                if (profile.properties.containsKey(args[2])) {
                    val previousValue = profile.properties[args[2]]
                    profile.properties[args[2]] = args[3]
                    sender.sendMessage(MessageConfiguration.getProfileChangePropertyMessage(args[2], previousValue.toString(), args[3], profile.name))
                    profile.save()
                }
            }
            ProfileType.VICTIM.friendlyName -> {
                TODO()
            }
        }
        return true
    }
}