package logan.pickpocket.commands;

import logan.pickpocket.main.PickpocketPlugin;
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

        if (!player.hasPermission(PickpocketPlugin.PICKPOCKET_EXEMPT))
        {
            player.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (objects.length == 0)
        {
            Profile profile = Profiles.get(player);
            boolean bool    = profile.getProfileConfiguration().getExemptSectionValue();
            profile.getProfileConfiguration().setExemptSection(bool = !bool);
            player.sendMessage(ChatColor.GRAY + "Your exempt status has been changed to " + bool + ".");
        }
        else
        {
            Player  otherPlayer        = Bukkit.getPlayer(objects[1].toString());
            boolean bool               = Boolean.parseBoolean(objects[0].toString());
            Profile otherPlayerProfile = Profiles.get(otherPlayer);
            otherPlayerProfile.getProfileConfiguration().setExemptSection(bool);
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s exempt status to " + bool + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your exempt status has been changed to " + bool + ".");
        }
    }
}
