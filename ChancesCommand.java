package logan.pickpocket.main;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/25/2015.
 */
public class ChancesCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, Command command, String label, Object... args) {
        List<Profile> profileList = (List<Profile>) args[0];
        Profile profile = ProfileHelper.getLoadedProfile(player, profileList);
        for (PickpocketItem pickpocketItem : PickpocketItem.values()) {
            player.sendMessage(pickpocketItem.getName() + ": " + ChatColor.YELLOW + pickpocketItem.calculateExperienceBasedChance(profile.getExperience()) + "%");
        }
        return true;
    }
}
