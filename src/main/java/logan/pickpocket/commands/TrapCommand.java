package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.managers.TrapInventoryManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Opens the trap inventory menu.
 */
public class TrapCommand extends BasicCommand<Player> {

    public TrapCommand() {
        super("trap", "pickpocket.use", 0, 0,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                List.of(),
                "Usage:\n/pickpocket trap");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        TrapInventoryManager.openFor(PickpocketUser.get(sender));
        return true;
    }
}
