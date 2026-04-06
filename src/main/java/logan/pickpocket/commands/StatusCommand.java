package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.Config;
import org.bukkit.entity.Player;

import java.util.Collections;

public class StatusCommand extends BasicCommand<Player> {

    public StatusCommand() {
        super("status", "pickpocket.status", 0, 0,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                Collections.emptyList(),
                "Usage:\n/pickpocket status");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        sender.sendMessage("Pickpocketing is " + Config.isPickpocketingEnabled() + ".");
        return true;
    }
}
