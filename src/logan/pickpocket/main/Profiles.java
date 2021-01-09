package logan.pickpocket.main;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/17/2015.
 */
public abstract class Profiles {
    public static PickpocketUser get(Player player) {
        for (PickpocketUser user : PickpocketPlugin.getProfiles()) {
            if (user.getUuid().equals(player.getUniqueId())) {
                return user;
            }
        }

        PickpocketUser profile = new PickpocketUser(player.getUniqueId());
        PickpocketPlugin.addProfile(profile);
        return profile;
    }
}
