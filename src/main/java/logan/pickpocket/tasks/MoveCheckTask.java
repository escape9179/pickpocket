package logan.pickpocket.tasks;

import logan.pickpocket.main.MoveCheck;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class MoveCheckTask implements Runnable {

    private final Plugin plugin;

    public MoveCheckTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 5L, 5L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(Objects::nonNull)
                .forEach(MoveCheck::check);
    }
}
