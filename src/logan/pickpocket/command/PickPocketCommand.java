package logan.pickpocket.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/15/2015.
 */
public abstract class PickPocketCommand {
    public boolean execute(Player player) {
        return true;
    }

    public boolean execute(Player player, Command command, String label, Object... args) {
        return true;
    }
}
