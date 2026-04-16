package logan.pickpocket.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks active pickpocket cooldown timers per player.
 */
public class CooldownManager {

    private static final Map<Player, Integer> cooldowns = new ConcurrentHashMap<>();

    private CooldownManager() {
    }

    /**
     * @return mutable cooldown map in seconds
     */
    public static Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }

    /**
     * Adds or replaces a cooldown for a player.
     *
     * @param player player to cooldown
     * @param duration cooldown duration in seconds
     */
    public static void addCooldown(Player player, int duration) {
        cooldowns.put(player, duration);
    }
    
    /**
     * @param player player to check
     * @return true when player currently has a cooldown
     */
    public static boolean hasCooldown(Player player) {
        return cooldowns.containsKey(player);
    }

}
