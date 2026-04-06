package logan.pickpocket.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.plugin.Plugin;

public class EssentialsHook {

    private static Essentials essentials;

    private EssentialsHook() {}

    public static void initialize(Plugin plugin) {
        var essentialsPlugin = plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin instanceof Essentials ess) {
            essentials = ess;
        } else {
            plugin.getLogger().info("Essentials not found. People can steal from AFK players!");
        }
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static boolean isEssentialsPresent() {
        return essentials != null;
    }
}
