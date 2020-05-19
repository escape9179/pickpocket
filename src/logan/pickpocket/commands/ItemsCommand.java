package logan.pickpocket.commands;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/18/2015.
 */
public class ItemsCommand implements PickpocketCommand {

    private Pickpocket pickpocket;

    public ItemsCommand(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
    }

    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects) {
        Profile profile = Profiles.get(player, profiles, pickpocket);
        profile.openPickpocketItemInventory();
    }
}
