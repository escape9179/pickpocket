package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/18/2015.
 */
public class ItemsCommand implements PickpocketCommand {
    @Override
    public void execute(Player player, List<Profile> profiles, Object... objects) {
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        profile.openPickpocketItemInventory();
    }
}
