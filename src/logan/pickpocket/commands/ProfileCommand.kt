package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ProfileCommand : BasicCommand<CommandSender>(
    "profile",
    1..3,
    listOf(String::class, String::class, String::class),
    "pickpocket",
    SenderTarget.BOTH,
    "pickpocket.profile.view",
    """
        Usage:
        /pickpocket profile view
        /pickpocket profile create <thief|victim> <profile>
        /pickpocket profile remove <thief|victim> <profile>
        /pickpocket profile edit <thief|victim> <profile> <property> <value>
        /pickpocket profile assign <thief|victim> <profile> <player>
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

class ProfileAssignCommand : BasicCommand<CommandSender>(
    "assign",
    2..2,
    listOf(String::class, String::class),
    "profile",
    SenderTarget.BOTH,
    "pickpocket.profile.assign",
    """
        Usage:
        /pickpocket profile assign <profile> <player>
    """.trimIndent()
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        val user = PickpocketUser.get(Bukkit.getPlayer(args[1]) ?: sender.run {
            sendMessage(MessageConfiguration.playerNotFoundMessage)
            return true
        })
        return if (user.assignThiefProfile(args[0])) {
            sender.sendMessage(MessageConfiguration.getProfileAssignSuccessMessage(args[0], user.bukkitPlayer!!))
            true
        } else {
            sender.sendMessage(MessageConfiguration.getProfileAssignFailureMessage(args[0], user.bukkitPlayer!!))
            true
        }
    }
}