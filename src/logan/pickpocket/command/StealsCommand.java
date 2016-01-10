package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Tre on 12/25/2015.
 */
public class StealsCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, List<Profile> profiles) {
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        profile.getPlayer().sendMessage(ChatColor.GRAY + "Times Stolen: " + ChatColor.GREEN + NumberFormat.getInstance().format(profile.getTimesStolen()));
        return true;
    }
}
