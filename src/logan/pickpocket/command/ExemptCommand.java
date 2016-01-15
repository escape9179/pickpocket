package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class ExemptCommand implements PickpocketCommand {
    @Override
    public void execute(Player player, List<Profile> profiles, Object... args) {
        if (args[1] == null) {
            Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
            boolean bool = Boolean.parseBoolean(args[0].toString());
            profile.setStealExempt(bool);
            player.sendMessage(ChatColor.GRAY + "Your exempt status has been changed to " + bool + ".");
        } else {
            Player otherPlayer = (Player) args[0];
            boolean bool = Boolean.parseBoolean(args[1].toString());
            Profile otherPlayerProfile = ProfileHelper.getLoadedProfile(otherPlayer, profiles);
            otherPlayerProfile.setStealExempt(bool);
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s exempt status to " + bool + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your exempt status has been changed to " + bool + ".");
        }
    }
}
