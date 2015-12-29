package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/18/2015.
 */
public class ItemsCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, List<Profile> profiles) {
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        profile.openPickpocketItemInventory();
        return true;
    }
}
