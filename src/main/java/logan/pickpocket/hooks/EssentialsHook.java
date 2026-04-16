package logan.pickpocket.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.plugin.Plugin;

/**
 * Optional integration with Essentials for AFK checks.
 */
public class EssentialsHook {

    private static Essentials essentials;

    private EssentialsHook() {}

    /**
     * Attempts to resolve Essentials from the plugin manager.
     *
     * @param plugin plugin instance used for lookup and logging
     */
    public static void initialize(Plugin plugin) {
        var essentialsPlugin = plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin instanceof Essentials ess) {
            essentials = ess;
        } else {
            plugin.getLogger().info("Essentials not found. People can steal from AFK players!");
        }
    }

    /**
     * @return resolved Essentials instance, or null when unavailable
     */
    public static Essentials getEssentials() {
        return essentials;
    }

    /**
     * @return true when Essentials integration is available
     */
    public static boolean isEssentialsPresent() {
        return essentials != null;
    }
}
