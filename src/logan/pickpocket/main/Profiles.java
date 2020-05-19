package logan.pickpocket.main;

import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 12/17/2015.
 */
public abstract class Profiles {
    public static Profile get(Player player, List<Profile> profiles, Pickpocket pickpocket) {
        for (Profile profile : profiles) {
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                profile.setPlayer(player);
                return profile;
            }
        }

        Profile profile=new Profile(player,pickpocket);
        Pickpocket.addProfile(profile);
        return profile;
    }
}
