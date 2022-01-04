package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.ProfileType
import logan.pickpocket.main.ThiefProfile
import logan.pickpocket.main.VictimProfile
import org.bukkit.entity.Player

class ProfileCreateCommand : BasicCommand<Player>(
    "create",
    2..2,
    listOf(String::class, String::class),
    "profile",
    SenderTarget.PLAYER,
    "pickpocket.profile.create",
    """
        Usage:
        /pickpocket profile create <thief|victim> <name>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
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
