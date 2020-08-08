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
public class BypassCommand implements PickpocketCommand {

    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects)
    {

        //TODO Fix array index out of bounds here

        if (!player.hasPermission(PickpocketPlugin.PICKPOCKET_BYPASS))
        {
            player.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (objects.length == 0)
        {
            Profile profile = Profiles.get(player);
            boolean bool    = profile.getProfileConfiguration().getBypassSectionValue();
            profile.getProfileConfiguration().setBypassSection(bool = !bool);
            player.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + bool + ".");
        }
        else
        {
            Player  otherPlayer        = Bukkit.getPlayer(objects[1].toString());
            boolean bool               = Boolean.parseBoolean(objects[0].toString());
            Profile otherPlayerProfile = Profiles.get(otherPlayer);
            otherPlayerProfile.getProfileConfiguration().setBypassSection(bool);
            player.sendMessage(ChatColor.GRAY + "Changed " + otherPlayer.getName() + "'s bypass status to " + bool + ".");
            otherPlayer.sendMessage(ChatColor.GRAY + "Your bypass status has been changed to " + bool + ".");
        }
    }
}
