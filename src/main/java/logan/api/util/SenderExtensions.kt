package logan.api.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

fun CommandSender.sendColoredMessage(message: String) {
    this.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
}

fun CommandSender.hasNoPermission(node: String): Boolean {
    return !this.hasPermission(node)
}