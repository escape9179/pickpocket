package logan.pickpocket.events;

import logan.guiapi.GUIAPI;
import logan.pickpocket.main.PickpocketPlugin;
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
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        GUIAPI.callInventoryCloseEvents(event);

        Player  player  = (Player) event.getPlayer();
        Profile profile = Profiles.get(player);
        if (profile.isStealing()) profile.setStealing(null);
    }
}
