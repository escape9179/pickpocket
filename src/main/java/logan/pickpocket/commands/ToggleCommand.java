package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.config.Config;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ToggleCommand extends BasicCommand<Player> {

    public ToggleCommand() {
        super("toggle", "pickpocket.admin.toggle", 0, 0,
                new String[0], SenderTarget.PLAYER, "admin",
                Collections.emptyList(),
                "Usage:\n/pickpocket admin toggle (no args)");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        boolean status = !Config.isPickpocketingEnabled();
        Config.setPickpocketingEnabled(status);
        sender.sendMessage(status ?
                MessageConfiguration.getPickpocketToggleOnMessage() :
                MessageConfiguration.getPickpocketToggleOffMessage());
        return true;
    }
}
