package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profile;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ProfileEditCommand extends BasicCommand<CommandSender> {

    public ProfileEditCommand() {
        super("edit", "pickpocket.profile.edit", 3, 3,
                new String[0], SenderTarget.BOTH, "profile",
                List.of(String.class, String.class, String.class, String.class),
                "Usage:\n/pickpocket profile edit <profile> <property> <value>");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        Profile profile = PickpocketPlugin.getProfileConfiguration().loadProfile(args[0]);
        if (profile == null) {
            sender.sendMessage(MessageConfiguration.getProfileNotFoundMessage(args[0]));
            return true;
        }
        if (profile.getProperties().containsKey(args[1])) {
            String previousValue = profile.getProperties().get(args[1]);
            profile.getProperties().put(args[1], args[2]);
            sender.sendMessage(
                    MessageConfiguration.getProfileChangePropertyMessage(
                            args[1],
                            String.valueOf(previousValue),
                            args[2],
                            profile.getName()
                    )
            );
            profile.save();
        }
        return true;
    }
}
