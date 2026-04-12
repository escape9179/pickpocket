package logan.pickpocket.listeners;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

/**
 * Starts pickpocket attempts when a fishing hook lands near a player.
 */
public class ProjectileHitListener implements Listener {

    /**
     * Locates nearby player victims and starts pickpocketing from the shooter.
     *
     * @param event projectile hit event
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof FishHook hook))
            return;
        if (!(hook.getShooter() instanceof Player shooterPlayer))
            return;

        PickpocketUser predator = PickpocketUser.get(shooterPlayer);

        List<Entity> nearbyEntities = hook.getNearbyEntities(1.0, 1.0, 1.0);
        Player victimPlayer = null;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player p) {
                victimPlayer = p;
                break;
            }
        }
        if (victimPlayer == null)
            return;

        PickpocketUser victim = PickpocketUser.get(victimPlayer);
        predator.doPickpocket(victim);
    }
}
