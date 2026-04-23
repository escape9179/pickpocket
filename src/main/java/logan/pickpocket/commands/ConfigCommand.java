package logan.pickpocket.commands;

import java.util.List;

import org.bukkit.entity.Player;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.managers.PickpocketConfigMenuManager;

/**
 * Opens the plugin configuration menu.
 */
public final class ConfigCommand extends BasicCommand<Player> {

    public ConfigCommand() {
        super("config", "pickpocket.config", 0, 0,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                List.of(),
                "Usage:\n/pickpocket config");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        PickpocketConfigMenuManager.openFor(sender);
        return true;
    }
}
