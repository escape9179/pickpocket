package logan.pickpocket.main;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/17/2015.
 */
public abstract class Profiles {
    public static PickpocketUser get(Player player) {
        for (PickpocketUser profile : PickpocketPlugin.getProfiles()) {
            if (profile.getUuid().equals(player.getUniqueId())) {
                return profile;
            }
        }

        PickpocketUser profile = new PickpocketUser(player.getUniqueId());
        PickpocketPlugin.addProfile(profile);

        return profile;
    }
}
