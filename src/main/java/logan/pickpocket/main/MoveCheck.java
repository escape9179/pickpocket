package logan.pickpocket.main;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MoveCheck {

    private static final Map<UUID, Location> playerLocationMap = new HashMap<>();

    private MoveCheck() {
    }

    public static void check(Player player) {
        Location previousLocation = playerLocationMap.get(player.getUniqueId());
        if (previousLocation == null) {
            playerLocationMap.put(player.getUniqueId(), player.getLocation());
            return;
        }

        Location currentLocation = player.getLocation();

        // If the player is standing on the same block don't do anything.
        if (previousLocation.getBlockX() == currentLocation.getBlockX()
                && previousLocation.getBlockY() == currentLocation.getBlockY()
                && previousLocation.getBlockZ() == currentLocation.getBlockZ()) {
            return;
        } else {
            playerLocationMap.put(player.getUniqueId(), currentLocation);
        }

        PickpocketUser user = PickpocketUser.get(player);

        // Check if the player is a predator.
        if (user.isPredator()) {
            PickpocketSessionManager.onPredatorMoved(player, user);
            return;
        }

        // Check if the player is a victim.
        if (user.isVictim()) {
            PickpocketSessionManager.onVictimMoved(user);
        }
    }
}
