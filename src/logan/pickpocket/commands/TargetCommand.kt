package logan.pickpocket.commands;

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.Profiles
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TargetCommand : BasicCommand<Player>(
    name = "target",
    argRange = 1..1,
    listOf(String::class),
    parentCommand = "pickpocket",
    target = SenderTarget.PLAYER,
    permissionNode = "pickpocket.target",
    usage = """
        Usage:
        /pickpocket target <name> - Pick-pocket player near you.
    """.trimIndent(),
) {

    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val victim = Bukkit.getPlayer(args[0]) ?: run {
            sender.sendMessage(MessageConfiguration.getPlayerNotFoundMessage())
            return true;
        }

        // Check that the victim can be seen and is at most 4 blocks away from the player
        if (sender.location.distance(victim.location) > 4) {
            sender.sendMessage(MessageConfiguration.getPlayerNotAccessibleMessage())
            return true
        }

        val senderProfile = Profiles.get(sender)
        val victimProfile = Profiles.get(victim)

        // Make sure both players have pick-pocketing enabled.
        if (!senderProfile.isParticipating) {
            sender.sendMessage(MessageConfiguration.getPickpocketDisabledMessage())
            return true
        }

        if (!victimProfile.isParticipating) {
            sender.sendMessage(MessageConfiguration.getPickpocketDisabledOtherMessage())
            return true
        }

        senderProfile.doPickpocket(victimProfile)
        return true
    }
}