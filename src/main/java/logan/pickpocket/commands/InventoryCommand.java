package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.managers.PickpocketInventoryManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Opens the pickpocket inventory blueprint editor.
 */
public class InventoryCommand extends BasicCommand<Player> {

    public InventoryCommand() {
        super("inventory", "pickpocket.defaults", 0, 0,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                List.of(),
                "Usage:\n/pickpocket inventory");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        PickpocketInventoryManager.openFor(PickpocketUser.get(sender));
        return true;
    }
}
