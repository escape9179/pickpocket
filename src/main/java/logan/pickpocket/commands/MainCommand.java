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
        super("pickpocket", "pickpocket", 0, 0,
                new String[] { "pp" }, SenderTarget.BOTH, null,
                Collections.emptyList(),
                "Usage:\n/pickpocket (no args)");
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        sender.sendMessage(
                "Pickpocket " + PickpocketPlugin.getInstance().getName() + " " + PickpocketPlugin.getPluginVersion()
                        + "\n" +
                        "/pickpocket toggle - Toggle server-wide pickpocketing.\n" +
                        "/pickpocket status - Check server-wide pickpocketing status.\n" +
                        "/pickpocket trap - Open your trap inventory.\n" +
                        "/pickpocket skills - View your skill levels in a menu.\n" +
                        "/pickpocket setskill <skill name> <level> - Set one of your skill levels.\n" +
                        "/pickpocket reload - Reload the plugin.\n" +
                        "/pickpocket debug giverandom <amount> - Give yourself random items.");
        return true;
    }
}
