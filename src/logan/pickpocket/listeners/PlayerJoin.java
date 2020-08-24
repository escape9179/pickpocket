package logan.pickpocket.listeners;

import logan.config.MessageConfiguration;
import logan.config.PickpocketConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerJoin implements Listener {
    public PlayerJoin() {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        Profile profile = Profiles.get(player);

        // return without showing status message
        if (!PickpocketConfiguration.isShowStatusOnLoginEnabled()) return;

        // show status message
        if (profile.isParticipating()) {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PARTICIPATING_TRUE_NOTIFICATION_KEY));
        } else {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PARTICIPATING_FALSE_NOTIFICATION_KEY));
        }
    }
}
