package logan.pickpocket.commands;

import java.util.List;

import org.bukkit.entity.Player;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.managers.ItemRarityListMenuManager;

/**
 * Opens a paginated menu of configured item rarities.
 */
public final class ListCommand extends BasicCommand<Player> {

    public ListCommand() {
        super("list", "pickpocket.list", 0, 0,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                List.of(),
                "Usage:\n/pickpocket list");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        ItemRarityListMenuManager.openFor(sender);
        return true;
    }
}
