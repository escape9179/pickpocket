package logan.api.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class FunctionUtils {

    private FunctionUtils() {
    }

    public static void sendMessage(CommandSender sender, String message, boolean translateColorCodes) {
        if (translateColorCodes) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            sender.sendMessage(message);
        }
    }
}
