package logan.pickpocket.command;

import logan.pickpocket.main.PickpocketItemInventory;
import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/18/2015.
 */
public class ItemsCommand extends PickPocketCommand {
    @Override
    public boolean execute(Player player, Command command, String label, Object... args) {
        List<Profile> profiles = (List<Profile>) args[0];
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        PickpocketItemInventory.open(profile);
        return true;
    }
}
