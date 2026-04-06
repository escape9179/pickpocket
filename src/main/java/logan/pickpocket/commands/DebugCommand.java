package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import org.bukkit.entity.Player;

import java.util.Collections;

public class DebugCommand extends BasicCommand<Player> {

    public DebugCommand() {
        super("debug", "pickpocket.debug", 1, 1,
                new String[] {}, SenderTarget.PLAYER, "pickpocket",
                Collections.emptyList(),
                "/pickpocket debug giverandom");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        sender.sendMessage("Usage:\n/pickpocket debug giverandom <amount>");
        return true;
    }
}
