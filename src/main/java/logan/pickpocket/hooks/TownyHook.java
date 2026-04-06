package logan.pickpocket.hooks;

import org.bukkit.plugin.Plugin;

public class TownyHook {

    private static boolean townyPresent;

    private TownyHook() {}

    public static void initialize(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Towny") != null) {
            townyPresent = true;
            plugin.getLogger().info("Towny rules in effect. Change them in config.yml.");
        }
    }

    public static boolean isTownyPresent() {
        return townyPresent;
    }
}
