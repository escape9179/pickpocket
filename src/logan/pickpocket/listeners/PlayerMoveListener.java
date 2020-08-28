package logan.pickpocket.listeners;

import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        PickpocketUser playerProfile = Profiles.get(event.getPlayer());

        // Check if the player is a predator
        if (playerProfile.isPredator()) {
            PickpocketUser victimProfile = playerProfile.getVictim();
            if (playerProfile.isPlayingMinigame()) {
                playerProfile.getMinigameModule().stopMinigame();
            }
            if (playerProfile.isRummaging()) {
                playerProfile.getPlayer().closeInventory();
                playerProfile.setRummaging(false);
            }
            event.getPlayer().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_ON_MOVE_WARNING_KEY));
            playerProfile.setVictim(null);
            victimProfile.setPredator(null);
            return;
        }

        // Check if the player is a victim
        if (playerProfile.isVictim()) {
            PickpocketUser predatorProfile = playerProfile.getPredator();
            if (predatorProfile.isPlayingMinigame()) {
                predatorProfile.getMinigameModule().stopMinigame();
            }
            if (predatorProfile.isRummaging()) {
                predatorProfile.getPlayer().closeInventory();
                predatorProfile.setRummaging(false);
            }
            playerProfile.getPredator().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY));
            playerProfile.setPredator(null);
            predatorProfile.setVictim(null);
        }
    }
}
