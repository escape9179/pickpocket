package logan.api.command;

import logan.api.util.FunctionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandDispatcher implements org.bukkit.command.CommandExecutor {

    private static final Set<BasicCommand<?>> registeredCommands = new HashSet<>();

    private CommandDispatcher() {
    }

    public static void registerCommand(BasicCommand<?> command) {
        registeredCommands.add(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return handleCommand(sender, command, label, args);
    }

    public static boolean handleCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        CommandSearchResult result = searchForSubCommandRecursively(label, args);
        if (result == null) return true;

        BasicCommand<?> foundCommand = result.command;
        String[] remainingArgs = result.args;

        if (!sender.hasPermission(foundCommand.getPermissionNode())) {
            FunctionUtils.sendMessage(sender, "&cNo permission.", true);
            return true;
        }

        SenderTarget commandTarget = (sender instanceof Player) ? SenderTarget.PLAYER : SenderTarget.CONSOLE;

        if (!isValidTarget(commandTarget, foundCommand.getTarget())) {
            sender.sendMessage(ChatColor.RED + "You cannot perform this command as " + sender.getClass().getSimpleName());
            return true;
        }

        if (!isValidArgLength(remainingArgs.length, foundCommand.getMinArgs(), foundCommand.getMaxArgs())) {
            if (foundCommand.getUsage() != null) {
                sender.sendMessage(foundCommand.getUsage());
            }
            return true;
        }

        if (!foundCommand.getArgTypes().isEmpty() && !isCorrectArgTypeList(remainingArgs, foundCommand.getArgTypes())) {
            if (foundCommand.getUsage() != null) {
                sender.sendMessage(foundCommand.getUsage());
            }
            return true;
        }

        if (foundCommand.getTarget() == SenderTarget.PLAYER) {
            @SuppressWarnings("unchecked")
            BasicCommand<Player> playerCommand = (BasicCommand<Player>) foundCommand;
            return playerCommand.run((Player) sender, remainingArgs, null);
        } else {
            @SuppressWarnings("unchecked")
            BasicCommand<CommandSender> senderCommand = (BasicCommand<CommandSender>) foundCommand;
            return senderCommand.run(sender, remainingArgs, null);
        }
    }

    private static CommandSearchResult searchForSubCommandRecursively(String label, String[] args) {
        BasicCommand<?> foundCommand = null;
        for (BasicCommand<?> cmd : registeredCommands) {
            if (cmd.getName().equals(label) || Arrays.asList(cmd.getAliases()).contains(label)) {
                foundCommand = cmd;
                break;
            }
        }

        if (foundCommand == null) return null;
        if (args.length == 0) return new CommandSearchResult(foundCommand, args);

        CommandSearchResult subResult = searchForSubCommandRecursively(args[0], Arrays.copyOfRange(args, 1, args.length));
        return subResult != null ? subResult : new CommandSearchResult(foundCommand, args);
    }

    private static boolean isValidTarget(SenderTarget received, SenderTarget expected) {
        return received == expected || expected == SenderTarget.BOTH;
    }

    private static boolean isValidArgLength(int receivedArgNum, int minArgs, int maxArgs) {
        return receivedArgNum >= minArgs && receivedArgNum <= maxArgs;
    }

    private static boolean isCorrectArgTypeList(String[] receivedArgs, List<Class<?>> requiredArgTypes) {
        for (int i = 0; i < receivedArgs.length; i++) {
            Class<?> required = requiredArgTypes.get(i);
            if (required == Integer.class || required == int.class) {
                try {
                    Integer.parseInt(receivedArgs[i]);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if (required == Double.class || required == double.class) {
                try {
                    Double.parseDouble(receivedArgs[i]);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if (required != String.class) {
                return false;
            }
        }
        return true;
    }

    private static class CommandSearchResult {
        final BasicCommand<?> command;
        final String[] args;

        CommandSearchResult(BasicCommand<?> command, String[] args) {
            this.command = command;
            this.args = args;
        }
    }
}
