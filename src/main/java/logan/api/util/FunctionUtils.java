package logan.api.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class FunctionUtils {

    private FunctionUtils() {
    }

    public static long secondsToTicks(long seconds) {
        return 20 * seconds;
    }

    public static void sendMessage(CommandSender sender, String message, boolean translateColorCodes) {
        if (translateColorCodes) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            sender.sendMessage(message);
        }
    }

    public static boolean hasNoPermission(CommandSender sender, String node) {
        return !sender.hasPermission(node);
    }

    public static boolean playerEquals(Player player, Player other) {
        if (other == null) return false;
        return player.getUniqueId().equals(other.getUniqueId());
    }

    public static Location blockLocation(Player player) {
        return toBlockLocation(player.getLocation());
    }

    public static double distanceFrom(Player player, Location location) {
        return player.getLocation().distance(location);
    }

    public static Location toBlockLocation(Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public static Color toBukkitColor(String colorString) {
        String[] parts = colorString.split(" ");
        if (parts.length == 1) {
            return Color.fromRGB(Integer.decode(parts[0]));
        } else {
            return Color.fromRGB(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        }
    }
}
