package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.managers.PickpocketSessionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DebugRevealCommand extends BasicCommand<CommandSender> {

    private static final String USAGE = "/pickpocket debug reveal <true|false>";

    public DebugRevealCommand() {
        super("reveal", "pickpocket.debug.reveal", 1, 1,
                new String[0], SenderTarget.BOTH, "debug",
                Collections.emptyList(),
                USAGE);
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        String input = args[0].toLowerCase();
        if (!input.equals("true") && !input.equals("false")) {
            sender.sendMessage(ChatColor.RED + USAGE);
            return true;
        }
        boolean enabled = Boolean.parseBoolean(input);
        PickpocketSessionManager.setDebugRevealEnabled(enabled);
        sender.sendMessage(ChatColor.YELLOW + "Debug reveal is now " + enabled + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("true", "false");
        }
        return Collections.emptyList();
    }
}
