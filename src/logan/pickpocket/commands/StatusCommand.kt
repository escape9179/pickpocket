package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player

class StatusCommand : BasicCommand<Player>(
    "status",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.use",
    usage = """
        Usage:
        /pickpocket status (no args)"
    """.trimIndent()
) {

    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val user = PickpocketUser.get(sender)
        sender.sendMessage("""
            Can pickpocket/get pickpocketed: ${user.isParticipating}
            Can bypass cooldown: ${user.playerConfiguration.bypassSectionValue}
        """.trimIndent())
        return true
    }
}