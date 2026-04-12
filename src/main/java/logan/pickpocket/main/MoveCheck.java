package logan.pickpocket.main;

import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Detects block-level movement and notifies session logic.
 */
public final class MoveCheck {

    private static final Map<UUID, Location> playerLocationMap = new HashMap<>();

    private MoveCheck() {
    }

    /**
     * Compares current and previous player locations and reacts to movement.
     *
     * @param player player to inspect
     */
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
        var session = PickpocketSessionManager.getSession(user);
        if (session == null) {
            return;
        }
        if (session.isThief(user)) {
            PickpocketSessionManager.onPredatorMoved(player, user);
            return;
        }
        if (session.isVictim(user)) {
            PickpocketSessionManager.onVictimMoved(user);
        }
    }
}
