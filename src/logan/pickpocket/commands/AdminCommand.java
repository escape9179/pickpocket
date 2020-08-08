package logan.pickpocket.commands;

import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class AdminCommand implements PickpocketCommand
{
    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects)
    {
        Profile profile = Profiles.get(player);
        boolean value   = profile.getProfileConfiguration().getAdminSectionValue();
        Profiles.get(player).getProfileConfiguration().setAdminSection(value = !value);
        player.sendMessage(ChatColor.GRAY + "Pickpocket Admin status set to " + value + ".");
    }
}
