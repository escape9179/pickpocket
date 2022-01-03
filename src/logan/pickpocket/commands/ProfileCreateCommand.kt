package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
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
    "pickpocket.admin.profile.create",
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
                    if (result) "Successfully created thief profile ${args[1]}."
                    else "Error: Profile ${args[1]} already exists."
                )
            }
            ProfileType.VICTIM.friendlyName -> {
                val result = VictimProfile(args[1]).save()
                sender.sendMessage(
                    if (result) "Successfully created victim profile ${args[1]}."
                    else "Error: Profile ${args[1]} already exists."
                )
            }
        }
        return true
    }
}
