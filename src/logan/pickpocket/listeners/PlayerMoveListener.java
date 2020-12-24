package logan.pickpocket.listeners;

import logan.pickpocket.config.MessageConfiguration;
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
                playerProfile.getCurrentMinigame().stop();
            }
            if (playerProfile.isRummaging()) {
                playerProfile.getBukkitPlayer().closeInventory();
                playerProfile.setRummaging(false);
            }
            player.sendMessage(MessageConfiguration.getPickpocketOnMoveWarningMessage());
            playerProfile.setVictim(null);
            victimProfile.setPredator(null);
            return;
        }

        // Check if the player is a victim.
        if (playerProfile.isVictim()) {
            PickpocketUser predatorProfile = playerProfile.getPredator();
            if (predatorProfile.isPlayingMinigame()) {
                predatorProfile.getCurrentMinigame().stop();
            }
            if (predatorProfile.isRummaging()) {
                predatorProfile.getBukkitPlayer().closeInventory();
                predatorProfile.setRummaging(false);
            }
            playerProfile.getLastPredator().sendMessage(MessageConfiguration.getPickpocketOnMoveOtherWarningMessage());
            playerProfile.setPredator(null);
            predatorProfile.setVictim(null);
        }
    }
}
