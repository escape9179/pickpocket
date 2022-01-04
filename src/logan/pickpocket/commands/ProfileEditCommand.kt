package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.ProfileType
import org.bukkit.entity.Player

class ProfileEditCommand : BasicCommand<Player>(
    "edit",
    4..4,
    listOf(String::class, String::class, String::class, String::class),
    "profile",
    SenderTarget.PLAYER,
    "pickpocket.profile.edit",
    """
        Usage:
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        when (args[0]) {
            ProfileType.THIEF.friendlyName -> {
                val profile = PickpocketPlugin.profileConfiguration.loadThiefProfile(args[1]) ?: run {
                    sender.sendMessage("Couldn't find profile with name '${args[1]}'")
                    return true
                }
                if (profile.properties.containsKey(args[2])) {
                    val previousValue = profile.properties[args[2]]
                    profile.properties[args[2]] = args[3]
                    sender.sendMessage("Changed property ${args[2]} from $previousValue to ${args[3]} in profile ${profile.name}")
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