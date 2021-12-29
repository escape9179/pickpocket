package logan.pickpocket.user

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.entity.Player

class WorldGuardUtil {
    companion object {
        fun isPickpocketingAllowed(player: Player): Boolean {
            if (!PickpocketPlugin.isWorldGuardPresent) return true
            val localPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
            val container = WorldGuard.getInstance().platform.regionContainer
            val query = container.createQuery()
            val worldEditLocation = BukkitAdapter.adapt(player.location)
            return query.testState(worldEditLocation, localPlayer, PickpocketPlugin.PICKPOCKET_FLAG)
        }
    }
}