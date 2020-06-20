package logan.pickpocket.main;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/17/2015.
 */
public abstract class Profiles {
    public static Profile get(Player player, List<Profile> profiles) {
        for (Profile profile : profiles) {
            if (profile.getPlayer().equals(player)) {
                return profile;
            }
        }
        return null;
    }

    public static void save(List<Profile> profiles) {
        for (Profile profile : profiles) profile.save();
    }
}
