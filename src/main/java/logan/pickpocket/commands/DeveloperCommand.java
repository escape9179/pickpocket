package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import org.bukkit.entity.Player;

import java.util.Collections;

public class DeveloperCommand extends BasicCommand<Player> {

    public DeveloperCommand() {
        super("developer", "pickpocket.developer", 1, 1,
                new String[]{"dev"}, SenderTarget.PLAYER, "pickpocket",
                Collections.emptyList(),
                "/pickpocket developer giverandom");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
