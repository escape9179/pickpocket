package logan.pickpocket.events;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profiles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerJoin implements Listener {
    public PlayerJoin() {
        Pickpocket.registerListener(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Profiles.get(event.getPlayer());
    }
}
