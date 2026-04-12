package logan.pickpocket.tasks;

import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.managers.SessionEndReason;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PickpocketTask extends BukkitRunnable {

    private final PickpocketUser predator;
    private final PickpocketUser victim;
    private final int delayTicks;
    private final Location startLocation;
    private final Location victimStartLocation;

    private int ticksPassed = 0;

    public PickpocketTask(PickpocketUser predator, PickpocketUser victim, int delayTicks, Location startLocation,
            Location victimStartLocation) {
        this.predator = predator;
        this.victim = victim;
        this.delayTicks = delayTicks;
        this.startLocation = startLocation;
        this.victimStartLocation = victimStartLocation;
    }

    @Override
    public void run() {
        Player player = predator.getBukkitPlayer();
        Player targetPlayer = victim.getBukkitPlayer();

        if (player == null || !player.isOnline() || targetPlayer == null || !targetPlayer.isOnline()) {
            PickpocketSessionManager.unlinkSession(predator, SessionEndReason.TASK_CANCELLED);
            cancel();
            return;
        }

        if (player.getLocation().distanceSquared(startLocation) > 0.5) {
            predator.sendMessage(MessageConfig.getPickpocketCancelledMovedMessage());
            PickpocketSessionManager.unlinkSession(predator, SessionEndReason.TASK_CANCELLED);
            cancel();
            return;
        }

        if (targetPlayer.getLocation().distanceSquared(victimStartLocation) > 0.5) {
            predator.sendMessage(MessageConfig.getPickpocketCancelledTargetMovedMessage());
            PickpocketSessionManager.unlinkSession(predator, SessionEndReason.TASK_CANCELLED);
            cancel();
            return;
        }

        if (ticksPassed >= delayTicks) {
            PickpocketSessionManager.openRummageInventory(predator, victim);
            cancel();
        }

        ticksPassed++;
    }
}
