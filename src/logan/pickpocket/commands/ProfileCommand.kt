package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player

class ProfileCommand : BasicCommand<Player>(
    "profile",
    1..3,
    listOf(String::class, String::class, String::class),
    "pickpocket",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile",
    """
        Usage:
        /pickpocket profile view
        /pickpocket profile create <thief|victim> <name>
        /pickpocket profile remove <thief|victim> <name>
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val profile = PickpocketUser.get(sender).findThiefProfile()
        sender.sendMessage("name: ${profile.name}\n${profile.properties.entries.joinToString("\n") { (k, v) -> k.plus(v) }}")
        return true
    }
}