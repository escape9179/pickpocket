package logan.pickpocket.hooks;

import org.bukkit.plugin.Plugin;

/**
 * Optional Towny integration toggle.
 */
public class TownyHook {

    private static boolean townyPresent;

    private TownyHook() {}

    /**
     * Detects whether Towny is installed and logs active behavior.
     *
     * @param plugin plugin instance used for lookup and logging
     */
    public static void initialize(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Towny") != null) {
            townyPresent = true;
            plugin.getLogger().info("Towny rules in effect. Change them in config.yml.");
        }
    }

    /**
     * @return true when Towny integration is available
     */
    public static boolean isTownyPresent() {
        return townyPresent;
    }
}
