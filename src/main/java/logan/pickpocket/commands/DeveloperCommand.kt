package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import org.bukkit.entity.Player

class DeveloperCommand : BasicCommand<Player>(
    "developer",
    1..1,
    listOf(String::class),
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.developer",
    usage = """
        /pickpocket developer giverandom
    """.trimIndent(),
    aliases = arrayOf("dev"),
    parentCommand = "pickpocket"
) {
}