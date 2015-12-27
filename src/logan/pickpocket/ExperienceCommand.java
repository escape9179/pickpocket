package logan.pickpocket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/25/2015.
 */
public class ExperienceCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, Command command, String label, Object... args) {
        List<Profile> profileList = (List<Profile>) args[0];
        Profile profile = ProfileHelper.getLoadedProfile(player, profileList);
        profile.getPlayer().sendMessage(ChatColor.GRAY + "Experience: " + ChatColor.GREEN + profile.getExperience());
        return true;
    }
}
