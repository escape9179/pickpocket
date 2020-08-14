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
public class ExemptCommand implements PickpocketCommand {


    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects)
    {
        if (objects.length == 0) {
            Profile profile = Profiles.get(player);
            boolean exemptStaus = !profile.getProfileConfiguration().getExemptSectionValue();
            profile.getProfileConfiguration().setExemptSection(exemptStaus);
            player.sendMessage(ChatColor.GRAY + "Your exempt status has been changed to " + exemptStaus + ".");
        } else {
            Player otherPlayer = Bukkit.getPlayer(objects[0].toString());
            Profile otherPlayerProfile = Profiles.get(otherPlayer);
            boolean exemptStatus = !otherPlayerProfile.getProfileConfiguration().getExemptSectionValue();
            otherPlayerProfile.getProfileConfiguration().setExemptSection(exemptStatus);
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s exempt status to " + exemptStatus + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your exempt status has been changed to " + exemptStatus + ".");
        }
    }
}
