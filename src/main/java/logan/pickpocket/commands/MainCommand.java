package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * This is the command run when there are no arguments provided and
 * the user is looking for more information about the plugin.
 */
public class MainCommand extends BasicCommand<CommandSender> {

    public MainCommand() {
        super("pickpocket", "pickpocket.use", 0, 0,
                new String[]{"pp"}, SenderTarget.BOTH, null,
                Collections.emptyList(),
                "Usage:\n/pickpocket (no args)");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        sender.sendMessage(
                "Pickpocket " + PickpocketPlugin.getInstance().getName() + " " + PickpocketPlugin.getPluginVersion() + "\n" +
                "/pickpocket admin notify - Toggle admin notifications for yourself\n" +
                "/pickpocket admin exempt [name] - Exempt yourself or another player from being stolen from\n" +
                "/pickpocket admin bypass [name] - Toggle cooldown bypass for yourself or another player\n" +
                "/pickpocket admin toggle - Toggle pickpocketing on or off.\n" +
                "/pickpocket admin status - View if pickpocketing is currently enabled or disabled.\n" +
                "/pickpocket profile create <name>\n" +
                "/pickpocket profile remove <name>\n" +
                "/pickpocket profile edit <profile> <property> <value>\n" +
                "/pickpocket profile assign <profile> <player>\n" +
                "Developer Commands:\n" +
                "/pickpocket developer giverandom <amount>"
        );
        return true;
    }
}
