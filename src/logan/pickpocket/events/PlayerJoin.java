package logan.pickpocket.events;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerJoin implements Listener {

    private Pickpocket pickpocket;

    public PlayerJoin(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
        pickpocket.getServer().getPluginManager().registerEvents(this, pickpocket);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Profile profile : pickpocket.getProfiles()) {
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return;
            }
        }

        pickpocket.addProfile(new Profile(player));
    }
}
