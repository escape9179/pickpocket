package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.Profiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Tre on 12/25/2015.
 */
public class StealsCommand implements PickpocketCommand {
    @Override
    public void execute(Player player, List<Profile> profiles, Object... objects) {
        Profile profile = Profiles.get(player, profiles);
        profile.getPlayer().sendMessage(ChatColor.GRAY + "Times Stolen: " + ChatColor.GREEN + NumberFormat.getInstance().format(profile.getTimesStolen()));
    }
}
