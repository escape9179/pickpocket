package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.Profiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class BypassCommand implements PickpocketCommand {
    @Override
    public void execute(Player player, List<Profile> profiles, Object... args) {

        if (args[1] == null) {
            Profile profile = Profiles.get(player, profiles);
            boolean bool = Boolean.parseBoolean(args[0].toString());
            profile.setCooldownBypass(bool);
            player.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + bool + ".");
        } else {
            boolean bool = Boolean.parseBoolean(args[0].toString());
            Player otherPlayer = Bukkit.getPlayer(args[1].toString());
            Profile otherPlayerProfile = Profiles.get(otherPlayer, profiles);
            otherPlayerProfile.setCooldownBypass(bool);
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s bypass status to " + bool + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + bool + ".");
        }
    }
}
