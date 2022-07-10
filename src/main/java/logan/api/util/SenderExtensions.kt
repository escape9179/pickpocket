package logan.api.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

fun CommandSender.sendMessage(message: String, translateColorCodes: Boolean) {
    if (translateColorCodes) this.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    else this.sendMessage(message)
}

fun CommandSender.hasNoPermission(node: String): Boolean {
    return !this.hasPermission(node)
}