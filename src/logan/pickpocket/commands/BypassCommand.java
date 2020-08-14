package logan.pickpocket.commands;

import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class BypassCommand implements PickpocketCommand {

    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects)
    {

        //TODO Fix array index out of bounds here
        if (objects.length == 0) {
            Profile profile = Profiles.get(player);
            boolean exemptStatus = !profile.getProfileConfiguration().getBypassSectionValue();
            profile.getProfileConfiguration().setBypassSection(exemptStatus);
            player.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + exemptStatus + ".");
        } else {
            Player otherPlayer = Bukkit.getPlayer(objects[0].toString());
            Profile otherPlayerProfile = Profiles.get(otherPlayer);
            boolean exemptStatus = !otherPlayerProfile.getProfileConfiguration().getExemptSectionValue();
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s bypass status to " + exemptStatus + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + exemptStatus + ".");
        }
    }
}
