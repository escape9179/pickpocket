package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/26/2015.
 */
public class GiveXPCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, Command command, String label, Object... args) {
        List<Profile> profileList = (List<Profile>) args[0];
        Profile profile = ProfileHelper.getLoadedProfile(player, profileList);
        profile.giveExperience(Integer.parseInt(String.valueOf(args[1])));
        return true;
    }
}
