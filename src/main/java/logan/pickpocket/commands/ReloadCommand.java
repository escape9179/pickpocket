package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.config.Config;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ReloadCommand extends BasicCommand<CommandSender> {

    public ReloadCommand() {
        super("reload", "pickpocket.reload", 0, 0,
                new String[0], SenderTarget.BOTH, "pickpocket",
                Collections.emptyList(),
                "Usage:\n/pickpocket reload (no args)");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        Config.reload();
        MessageConfiguration.reload();
        PickpocketPlugin.getProfileConfiguration().reload();
        sender.sendMessage(MessageConfiguration.getReloadNotificationMessage());
        return true;
    }
}
