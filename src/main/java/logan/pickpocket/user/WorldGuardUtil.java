package logan.pickpocket.user;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.entity.Player;

public final class WorldGuardUtil {

    private WorldGuardUtil() {
    }

    public static boolean isPickpocketingAllowed(Player player) {
        if (!PickpocketPlugin.isWorldGuardPresent()) return true;
        var localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        var worldEditLocation = BukkitAdapter.adapt(player.getLocation());
        return query.testState(worldEditLocation, localPlayer, PickpocketPlugin.getPickpocketFlag());
    }
}
