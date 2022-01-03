package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.main.ProfileType
import org.bukkit.entity.Player

class ProfileEditCommand : BasicCommand<Player>(
    "edit",
    4..4,
    listOf(String::class, String::class, String::class, String::class),
    "profile",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile.edit",
    """
        Usage:
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        when (args[0]) {
            ProfileType.THIEF.friendlyName -> {
                TODO()
            }
            ProfileType.VICTIM.friendlyName -> {
                TODO()
            }
        }
        return true
    }
}