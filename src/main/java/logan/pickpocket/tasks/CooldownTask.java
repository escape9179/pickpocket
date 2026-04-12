package logan.pickpocket.tasks;

import logan.pickpocket.managers.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * Scheduled task that decrements player cooldown timers once per second.
 */
public class CooldownTask implements Runnable {

    private final Plugin plugin;

    /**
     * @param plugin owning plugin
     */
    public CooldownTask(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts asynchronous cooldown ticking.
     */
    public void start() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 20L, 20L);
    }

    /**
     * Decrements cooldowns and removes expired entries.
     */
    @Override
    public void run() {
        Map<Player, Integer> cooldowns = CooldownManager.getCooldowns();
        for (Player player : cooldowns.keySet()) {
            int newDuration = cooldowns.get(player) - 1;
            cooldowns.put(player, newDuration);
            if (newDuration <= 0) {
                cooldowns.remove(player);
            }
        }
    }
}
