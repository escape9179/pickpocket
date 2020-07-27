package logan.pickpocket.main;

import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/17/2015.
 */
public abstract class Profiles {
    public static Profile get(Player player) {
        for (Profile profile : Pickpocket.getProfiles()) {
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                profile.setPlayer(player);
                return profile;
            }
        }

        Profile profile=new Profile(player);
        Pickpocket.addProfile(profile);
        return profile;
    }
}
