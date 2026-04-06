package logan.pickpocket.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardHook {

    private static StateFlag pickpocketFlag;
    private static boolean worldGuardPresent;

    private WorldGuardHook() {}

    public static void onLoad(Plugin plugin) {
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            worldGuardPresent = true;
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            pickpocketFlag = new StateFlag("pickpocket", true);
            registry.register(pickpocketFlag);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            plugin.getLogger().info("WorldGuard not found. Per-region pick-pocketing won't work.");
            worldGuardPresent = false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error registering WorldGuard flag.");
            e.printStackTrace();
        }
    }

    public static StateFlag getPickpocketFlag() {
        return pickpocketFlag;
    }

    public static boolean isWorldGuardPresent() {
        return worldGuardPresent;
    }

    public static boolean isPickpocketingAllowedAtPlayerRegion(Player player) {
        if (!isWorldGuardPresent()) return true;
        var localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        var worldEditLocation = BukkitAdapter.adapt(player.getLocation());
        return query.testState(worldEditLocation, localPlayer, getPickpocketFlag());
    }
}
