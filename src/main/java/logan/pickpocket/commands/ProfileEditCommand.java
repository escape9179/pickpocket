package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profile;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ProfileEditCommand extends BasicCommand<CommandSender> {

    private static final List<String> PROPERTY_NAMES = List.of("cooldown", "canUseFishingRod", "numberOfRummageItems");
    private static final List<String> BOOLEAN_VALUES = List.of("true", "false");

    public ProfileEditCommand() {
        super("edit", "pickpocket.profile.edit", 3, 3,
                new String[0], SenderTarget.BOTH, "profile",
                List.of(String.class, String.class, String.class, String.class),
                "Usage:\n/pickpocket profile edit <profile> <property> <value>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return new ArrayList<>(PickpocketPlugin.getProfileConfiguration().getProfileNames());
        } else if (args.length == 2) {
            return PROPERTY_NAMES;
        } else if (args.length == 3) {
            String property = args[1];
            if ("canUseFishingRod".equalsIgnoreCase(property)) {
                return BOOLEAN_VALUES;
            }
            return List.of();
        }
        return List.of();
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        Profile profile = PickpocketPlugin.getProfileConfiguration().loadProfile(args[0]);
        if (profile == null) {
            sender.sendMessage(MessageConfiguration.getProfileNotFoundMessage(args[0]));
            return true;
        }
        if (profile.hasProperty(args[1])) {
            String previousValue = profile.getProperty(args[1]);
            profile.setProperty(args[1], args[2]);
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
