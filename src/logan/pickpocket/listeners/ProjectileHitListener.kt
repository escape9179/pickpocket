package logan.pickpocket.listeners

import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class ProjectileHitListener : Listener {
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val hook = event.entity as? FishHook ?: return
        val predator = PickpocketUser.get(hook.shooter as? Player ?: return)
        val thiefProfile = predator.findThiefProfile() ?: predator.bukkitPlayer!!.run {
            sendMessage("You are not allowed to pickpocket.")
            return
        }
        if (!thiefProfile.canUseFishingRod) return
        val victim = PickpocketUser.get(hook.getNearbyEntities(1.0, 1.0, 1.0).firstOrNull() as? Player ?: return)
        predator.doPickpocket(victim)
    }
}