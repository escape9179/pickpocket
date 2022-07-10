package logan.api.util;

import org.bukkit.ChatColor;

public class ColorUtils {
    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
