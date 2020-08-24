package logan.pickpocket.commands;

import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class TargetCommand implements PickpocketCommand {

    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... args) {
        Player victim = Bukkit.getPlayer(args[0].toString());
        if (victim == null) {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PLAYER_NOT_FOUND_KEY));
            return;
        }
        // Check that the victim can be seen and is at most 5 blocks away from the player
        if (player.getLocation().distance(victim.getLocation()) > 4) {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PLAYER_NOT_ACCESSIBLE_KEY));
            return;
        }
        Profile playerProfile = Profiles.get(player);
        Profile victimProfile = Profiles.get(victim);
        // Make sure both players have pick-pocketing enabled.
        if (!playerProfile.isParticipating()) {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_DISABLED_KEY));
            return;
        }
        if (!victimProfile.isParticipating()) {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_DISABLED_OTHER_KEY));
            return;
        }
        playerProfile.performPickpocket(victim);
    }
}
