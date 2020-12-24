package logan.pickpocket.listeners;

import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerJoinListener implements Listener {
    public PlayerJoinListener() {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        PickpocketUser profile = Profiles.get(player);

        // return without showing status message
        if (!PickpocketPlugin.getPickpocketConfiguration().isShowStatusOnLoginEnabled()) return;

        // show status message
        if (profile.isParticipating())
            player.sendMessage(MessageConfiguration.getParticipatingTrueNotificationMessage());
        else
            player.sendMessage(MessageConfiguration.getParticipatingFalseNotificationMessage());
    }
}
