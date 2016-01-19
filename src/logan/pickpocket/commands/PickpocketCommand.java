package logan.pickpocket.commands;

import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/15/2015.
 */
public interface PickpocketCommand {
    void execute(Player player, List<Profile> profiles, Object... args);
}
