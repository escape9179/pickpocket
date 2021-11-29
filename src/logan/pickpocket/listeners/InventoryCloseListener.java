package logan.pickpocket.listeners;

import logan.api.gui.GUIAPI;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryCloseListener implements Listener {

    public InventoryCloseListener() {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PickpocketUser profile = Profiles.get(player);

        if (profile.isPlayingMinigame()) {
            profile.getCurrentMinigame().stop();
            player.sendMessage(MessageConfiguration.getPickpocketUnsuccessfulMessage());
        }

        if (profile.isRummaging()) {
            profile.getOpenRummageInventory().close();
            profile.setRummaging(false);
            profile.getVictim().setPredator(null);
            profile.setVictim(null);
        }
    }
}
