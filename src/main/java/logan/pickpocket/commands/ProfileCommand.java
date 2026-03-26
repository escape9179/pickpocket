package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profile;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProfileCommand extends BasicCommand<CommandSender> {

    public ProfileCommand() {
        super("profile", "pickpocket.profile", 1, 3,
                new String[0], SenderTarget.BOTH, null,
                Collections.emptyList(),
                "Usage:\n" +
                "/pickpocket profile view\n" +
                "/pickpocket profile create <name>\n" +
                "/pickpocket profile remove <name>\n" +
                "/pickpocket profile edit <name> <property> <value>\n" +
                "/pickpocket profile assign <name> <player>");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        return true;
    }
}
