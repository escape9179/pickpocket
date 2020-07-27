package logan.pickpocket.events;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClose implements Listener {

    public InventoryClose() {
        Pickpocket.registerListener(this);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = Profiles.get(player);
        if (profile.isStealing()) profile.setStealing(null);
    }
}
