package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.Profile;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ProfileCreateCommand extends BasicCommand<CommandSender> {

    public ProfileCreateCommand() {
        super("create", "pickpocket.profile.create", 1, 1,
                new String[0], SenderTarget.BOTH, "profile",
                List.of(String.class, String.class),
                "Usage:\n/pickpocket profile create <profile>");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        boolean result = new Profile(args[0]).save();
        sender.sendMessage(result ?
                MessageConfiguration.getProfileCreateSuccessMessage(args[0]) :
                MessageConfiguration.getProfileErrorAlreadyExistsMessage(args[0]));
        return true;
    }
}
