package logan.api.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
fun Player.getRandomItemFromMainInventory() = inventory.getItem((Math.random() * (35 - 9) + 9).toInt())