package logan.pickpocket.listeners;

import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class InventoryCloseListener implements logan.api.listener.InventoryCloseListener {

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PickpocketUser profile = PickpocketUser.get(player);
        if (profile.isPlayingMinigame()) {
            profile.getCurrentMinigame().stop();
            player.sendMessage(MessageConfiguration.getPickpocketUnsuccessfulMessage());
        }
        if (profile.isRummaging()) {
            profile.setRummaging(false);
            profile.getVictim().setPredator(null);
            profile.setVictim(null);
        }
    }
}
