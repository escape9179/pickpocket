package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/15/2015.
 */
public abstract class PickPocketCommand {

    public boolean execute(Player player, List<Profile> profiles, Object... args) {
        return true;
    }

    public boolean execute(Player player, List<Profile> profiles) {
        return true;
    }
}
