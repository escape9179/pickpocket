package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/25/2015.
 */
public class ExperienceCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, List<Profile> profiles) {
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        profile.getPlayer().sendMessage(ChatColor.GRAY + "Experience: " + ChatColor.GREEN + profile.getExperience());
        return true;
    }
}
