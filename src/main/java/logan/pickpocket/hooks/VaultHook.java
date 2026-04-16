package logan.pickpocket.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Optional Vault economy integration.
 */
public class VaultHook {

    private static Economy economy;
    private static boolean vaultEnabled;

    private VaultHook() {}

    /**
     * Resolves the Vault economy provider when available.
     *
     * @param plugin plugin instance used for lookup and logging
     */
    public static void initialize(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().info("Vault not found. Players won't steal money when pick-pocketing.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            vaultEnabled = economy != null;
        }
    }

    /**
     * @return resolved economy provider, or null when unavailable
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * @return true when Vault and an economy provider are both available
     */
    public static boolean isVaultEnabled() {
        return vaultEnabled;
    }
}
