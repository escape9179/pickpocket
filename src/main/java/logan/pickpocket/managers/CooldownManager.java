package logan.pickpocket.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private static final Map<Player, Integer> cooldowns = new ConcurrentHashMap<>();

    private CooldownManager() {
    }

    public static Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }

    public static void addCooldown(Player player, int duration) {
        cooldowns.put(player, duration);
    }
    
    public static boolean hasCooldown(Player player) {
        return cooldowns.containsKey(player);
    }

}
