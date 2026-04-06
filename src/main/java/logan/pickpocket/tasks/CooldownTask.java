package logan.pickpocket.tasks;

import logan.pickpocket.managers.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class CooldownTask implements Runnable {

    private final Plugin plugin;

    public CooldownTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 20L, 20L);
    }

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
