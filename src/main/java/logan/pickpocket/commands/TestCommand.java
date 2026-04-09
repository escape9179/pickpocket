package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;

public class TestCommand extends BasicCommand<Player> {

    public TestCommand() {
        super("test", "pickpocket.test", 1, 1,
                new String[] {},
                SenderTarget.PLAYER,
                "pickpocket",
                Collections.emptyList(),
                "/pickpocket test <player>");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        if (args.length == 0) {
            sender.sendMessage("Usage:\n/pickpocket test <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage("You can't pickpocket yourself.");
            return true;
        }

        // Get sender's eye location and direction
        Location senderEyeLoc = sender.getEyeLocation();
        Vector direction = senderEyeLoc.getDirection().normalize();

        // Teleport the target 2 blocks ahead and make them face the sender
        Location targetLoc = senderEyeLoc.clone().add(direction.multiply(2));
        targetLoc.setPitch(0);
        targetLoc.setYaw(senderEyeLoc.getYaw() + 180);
        target.teleport(targetLoc.setDirection(senderEyeLoc.toVector().subtract(targetLoc.toVector())));

        sender.sendMessage("Shift right-click " + target.getName() + " to test pickpocketing.");

        return true;
    }
}
