package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import org.bukkit.entity.Player

class ProfileCommand : BasicCommand<Player>(
    "profile",
    3..3,
    listOf(String::class),
    "pickpocket",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile",
    """
        Usage:
        /pickpocket profile thief create <name>
        /pickpocket profile player create <name>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        TODO("Not yet implemented")
    }
}