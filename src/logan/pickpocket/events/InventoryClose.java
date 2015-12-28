package logan.pickpocket.events;

import logan.pickpocket.main.PickPocket;
import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryClose implements Listener {

    private PickPocket pickPocket;

    public InventoryClose(PickPocket pickPocket) {
        this.pickPocket = pickPocket;
        pickPocket.getServer().getPluginManager().registerEvents(this, pickPocket);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = ProfileHelper.getLoadedProfile(player, pickPocket.getProfiles());
        if (profile.isStealing()) profile.setStealing(null);
    }
}
