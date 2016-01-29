package logan.pickpocket.commands;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.profile.Profile;
import logan.pickpocket.main.Profiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class BypassCommand implements PickpocketCommand {

    private Pickpocket pickpocket;

    public BypassCommand(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
    }

    @Override
    public void execute(Player player, List<Profile> profiles, Object... args) {

        if (args[1] == null) {
            Profile profile = Profiles.get(player, profiles,pickpocket);
            boolean bool = Boolean.parseBoolean(args[0].toString());
            profile.getProfileConfiguration().setBypassSection(bool);
            player.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + bool + ".");
        } else {
            Player otherPlayer = Bukkit.getPlayer(args[1].toString());
            boolean bool = Boolean.parseBoolean(args[0].toString());
            Profile otherPlayerProfile = Profiles.get(otherPlayer, profiles,pickpocket);
            otherPlayerProfile.getProfileConfiguration().setBypassSection(bool);
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s bypass status to " + bool + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + bool + ".");
        }
    }
}
