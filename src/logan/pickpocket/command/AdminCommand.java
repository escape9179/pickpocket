package logan.pickpocket.command;

import logan.pickpocket.main.PickPocket;
import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class AdminCommand implements PickpocketCommand {
    @Override
    public void execute(Player player, List<Profile> profiles, Object... args) {
        if (player.isOp() || player.hasPermission(PickPocket.PICKPOCKET_ADMIN)) {
            boolean bool = Boolean.parseBoolean(args[0].toString());
            ProfileHelper.getLoadedProfile(player, profiles).setAdmin(bool);
            player.sendMessage(ChatColor.GRAY + "Pickpocket Admin status set to " + bool + ".");
        }
    }
}
