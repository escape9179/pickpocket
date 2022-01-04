package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player

class ProfileCommand : BasicCommand<Player>(
    "profile",
    1..3,
    listOf(String::class, String::class, String::class),
    "pickpocket",
    SenderTarget.PLAYER,
    "pickpocket.profile.view",
    """
        Usage:
        /pickpocket profile view
        /pickpocket profile create <thief|victim> <name>
        /pickpocket profile remove <thief|victim> <name>
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
)

class ProfileViewCommand : BasicCommand<Player>(
    "view",
    parentCommand = "profile",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.profile.view"
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val profile = PickpocketUser.get(sender).findThiefProfile() ?: sender.run {
            sendMessage(MessageConfiguration.getProfileNotAssignedMessage())
            return true
        }
        sender.sendMessage("name: ${profile.name}\n${profile.properties.entries.joinToString("\n") { (k, v) -> "$k: $v" }}")
        return true
    }
}