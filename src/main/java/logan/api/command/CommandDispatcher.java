package logan.api.command;

import logan.api.util.FunctionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandDispatcher implements org.bukkit.command.CommandExecutor, TabCompleter {

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

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        return handleTabComplete(sender, command, alias, args);
    }

    public static List<String> handleTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        // Find the root command for this label
        BasicCommand<?> rootCommand = findCommandByName(alias);
        if (rootCommand == null) return Collections.emptyList();

        // Walk the command tree, consuming args that match subcommand names
        BasicCommand<?> currentCommand = rootCommand;
        int argIndex = 0;

        while (argIndex < args.length) {
            String token = args[argIndex];
            boolean isLastArg = (argIndex == args.length - 1);

            // Look for a child subcommand whose parent matches the current command
            BasicCommand<?> childMatch = null;
            if (!isLastArg) {
                // Not the last arg — look for an exact match to descend into
                childMatch = findChildCommand(currentCommand.getName(), token);
            }

            if (childMatch != null) {
                currentCommand = childMatch;
                argIndex++;
            } else {
                // We've found the deepest command; remaining args are for this command
                break;
            }
        }

        // Build remaining args from the current position
        String[] remainingArgs = Arrays.copyOfRange(args, argIndex, args.length);
        String partial = remainingArgs.length > 0 ? remainingArgs[remainingArgs.length - 1].toLowerCase() : "";

        // If we're at a position where a subcommand name could be typed, suggest child command names
        if (remainingArgs.length <= 1) {
            List<String> suggestions = new ArrayList<>();

            // Add child subcommand names
            for (BasicCommand<?> cmd : registeredCommands) {
                if (currentCommand.getName().equals(cmd.getParentCommand())) {
                    if (sender.hasPermission(cmd.getPermissionNode())) {
                        suggestions.add(cmd.getName());
                        // Also add aliases
                        for (String cmdAlias : cmd.getAliases()) {
                            suggestions.add(cmdAlias);
                        }
                    }
                }
            }

            // Also add command-specific completions for arg position 0
            if (sender.hasPermission(currentCommand.getPermissionNode())) {
                suggestions.addAll(currentCommand.onTabComplete(sender, remainingArgs));
            }

            return filterSuggestions(suggestions, partial);
        }

        // Past the subcommand position — delegate to the current command's onTabComplete
        if (sender.hasPermission(currentCommand.getPermissionNode())) {
            List<String> suggestions = currentCommand.onTabComplete(sender, remainingArgs);
            return filterSuggestions(suggestions, partial);
        }

        return Collections.emptyList();
    }

    /**
     * Find a registered command by name or alias (no parent — i.e., a root command or any matching name).
     */
    private static BasicCommand<?> findCommandByName(String name) {
        for (BasicCommand<?> cmd : registeredCommands) {
            if (cmd.getName().equalsIgnoreCase(name) || containsIgnoreCase(cmd.getAliases(), name)) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * Find a child command whose parent matches the given parent name and whose name or alias matches the token.
     */
    private static BasicCommand<?> findChildCommand(String parentName, String token) {
        for (BasicCommand<?> cmd : registeredCommands) {
            if (parentName.equals(cmd.getParentCommand())) {
                if (cmd.getName().equalsIgnoreCase(token) || containsIgnoreCase(cmd.getAliases(), token)) {
                    return cmd;
                }
            }
        }
        return null;
    }

    private static boolean containsIgnoreCase(String[] array, String value) {
        for (String s : array) {
            if (s.equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    private static List<String> filterSuggestions(List<String> suggestions, String partial) {
        if (partial.isEmpty()) return suggestions;
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(partial))
                .collect(Collectors.toList());
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
