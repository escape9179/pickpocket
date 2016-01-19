package logan.pickpocket.events;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.profile.Profile;
import logan.pickpocket.main.Profiles;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClose implements Listener {

    private Pickpocket pickpocket;

    public InventoryClose(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
        pickpocket.getServer().getPluginManager().registerEvents(this, pickpocket);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = Profiles.get(player, pickpocket.getProfiles());
        if (profile.isStealing()) profile.setStealing(null);
    }
}
