package logan.pickpocket.listeners;

import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    private final Map<UUID, Location> playerLocationMap = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        Location previousLocation = playerLocationMap.get(player.getUniqueId());
        Location currentLocation = player.getLocation();

        if (previousLocation == null) {
            playerLocationMap.put(player.getUniqueId(), currentLocation);
            return;
        }

        // If the player is standing on the same block don't do anything.
        if (previousLocation.getBlockX() == currentLocation.getBlockX()
                && previousLocation.getBlockY() == currentLocation.getBlockY()
                && previousLocation.getBlockZ() == currentLocation.getBlockZ()) {
            return;
        } else {
            playerLocationMap.put(player.getUniqueId(), currentLocation);
        }

        PickpocketUser playerProfile = Profiles.get(player);

        // Check if the player is a predator.
        if (playerProfile.isPredator()) {
            PickpocketUser victimProfile = playerProfile.getVictim();
            if (playerProfile.isPlayingMinigame()) {
                playerProfile.getMinigameModule().stopMinigame();
            }
            if (playerProfile.isRummaging()) {
                playerProfile.getPlayer().closeInventory();
                playerProfile.setRummaging(false);
            }
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_ON_MOVE_WARNING_KEY));
            playerProfile.setVictim(null);
            victimProfile.setPredator(null);
            return;
        }

        // Check if the player is a victim.
        if (playerProfile.isVictim()) {
            PickpocketUser predatorProfile = playerProfile.getPredator();
            if (predatorProfile.isPlayingMinigame()) {
                predatorProfile.getMinigameModule().stopMinigame();
            }
            if (predatorProfile.isRummaging()) {
                predatorProfile.getPlayer().closeInventory();
                predatorProfile.setRummaging(false);
            }
            playerProfile.getRecentPredator().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY));
            playerProfile.setPredator(null);
            predatorProfile.setVictim(null);
        }
    }
}
