package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import org.bukkit.entity.Player

class DeveloperCommand : BasicCommand<Player>(
    "developer",
    "pickpocket.developer",
    1..1,
    target = SenderTarget.PLAYER,
        usage = """
        /pickpocket developer giverandom
    """.trimIndent(),
    aliases = arrayOf("dev"),
    parentCommand = "pickpocket"
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        TODO("Not yet implemented")
    }
}