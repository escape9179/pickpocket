package logan.api.command;

import org.bukkit.command.CommandSender;

public interface Command<T extends CommandSender> {
    boolean run(T sender, String[] args, Object data);

    default boolean run(T sender, String[] args) {
        return run(sender, args, null);
    }

    default boolean run(T sender) {
        return run(sender, new String[0], null);
    }
}
