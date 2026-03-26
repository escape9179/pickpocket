package logan.api.command;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BasicCommand<T extends CommandSender> implements Command<T> {
    private final String name;
    private final String permissionNode;
    private final int minArgs;
    private final int maxArgs;
    private final String[] aliases;
    private final SenderTarget target;
    private final String parentCommand;
    private final List<Class<?>> argTypes;
    private final String usage;

    protected BasicCommand(String name, String permissionNode, SenderTarget target) {
        this(name, permissionNode, 0, 0, new String[0], target, null, Collections.emptyList(), null);
    }

    protected BasicCommand(String name, String permissionNode, SenderTarget target,
                           int minArgs, int maxArgs) {
        this(name, permissionNode, minArgs, maxArgs, new String[0], target, null, Collections.emptyList(), null);
    }

    protected BasicCommand(String name, String permissionNode, SenderTarget target,
                           String[] aliases) {
        this(name, permissionNode, 0, 0, aliases, target, null, Collections.emptyList(), null);
    }

    protected BasicCommand(String name, String permissionNode, int minArgs, int maxArgs,
                           String[] aliases, SenderTarget target, String parentCommand,
                           List<Class<?>> argTypes, String usage) {
        this.name = name;
        this.permissionNode = permissionNode;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.aliases = aliases;
        this.target = target;
        this.parentCommand = parentCommand;
        this.argTypes = argTypes;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public String getPermissionNode() {
        return permissionNode;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public String[] getAliases() {
        return aliases;
    }

    public SenderTarget getTarget() {
        return target;
    }

    public String getParentCommand() {
        return parentCommand;
    }

    public List<Class<?>> getArgTypes() {
        return argTypes;
    }

    public String getUsage() {
        return usage;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) return false;
        if (other instanceof BasicCommand<?> otherCommand) {
            return otherCommand.name.equals(this.name);
        }
        return false;
    }
}
