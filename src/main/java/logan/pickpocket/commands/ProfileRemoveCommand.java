package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ProfileRemoveCommand extends BasicCommand<CommandSender> {

    public ProfileRemoveCommand() {
        super("remove", "pickpocket.profile.remove", 1, 1,
                new String[0], SenderTarget.BOTH, "profile",
                List.of(String.class, String.class),
                "Usage:\n/pickpocket profile remove <profile>");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        boolean result = PickpocketPlugin.getProfileConfiguration().removeProfile(args[0]);
        if (!result) {
            sender.sendMessage(MessageConfiguration.getProfileNotFoundMessage(args[0]));
            return false;
        }
        sender.sendMessage(MessageConfiguration.getProfileRemovedMessage(args[0]));
        return true;
    }
}
