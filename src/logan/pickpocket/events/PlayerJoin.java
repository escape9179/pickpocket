package logan.pickpocket.events;

import logan.pickpocket.main.PickPocket;
import logan.pickpocket.main.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerJoin {

    private PickPocket pickPocket;

    public PlayerJoin(PickPocket pickPocket) {
        this.pickPocket = pickPocket;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Profile profile : pickPocket.getProfiles()) {
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return;
            }
        }

        pickPocket.addProfile(new Profile(player));
    }
}
