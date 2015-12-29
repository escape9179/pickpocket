package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/26/2015.
 */
public class GiveXPCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player,List<Profile> profiles, Object... args) {
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        profile.giveExperience(Integer.parseInt(String.valueOf(args[1])));
        return true;
    }
}
