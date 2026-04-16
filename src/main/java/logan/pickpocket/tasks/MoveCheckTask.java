package logan.pickpocket.tasks;

import logan.pickpocket.main.MoveCheck;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Scheduled task that polls player movement for active sessions.
 */
public class MoveCheckTask implements Runnable {

    private final Plugin plugin;

    /**
     * @param plugin owning plugin
     */
    public MoveCheckTask(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts synchronous movement polling.
     */
    public void start() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 5L, 5L);
    }

    /**
     * Checks all online players for movement updates.
     */
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(Objects::nonNull)
                .forEach(MoveCheck::check);
    }
}
