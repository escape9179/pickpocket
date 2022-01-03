package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import org.bukkit.entity.Player

class ProfileCommand : BasicCommand<Player>(
    "profile",
    3..3,
    listOf(String::class, String::class, String::class),
    "pickpocket",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile",
    """
        Usage:
        /pickpocket profile create <thief|victim> <name>
        /pickpocket profile remove <thief|victim> <name>
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
)