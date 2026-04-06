package logan.pickpocket.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy;
    private static boolean vaultEnabled;

    private VaultHook() {}

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

    public static Economy getEconomy() {
        return economy;
    }

    public static boolean isVaultEnabled() {
        return vaultEnabled;
    }
}
