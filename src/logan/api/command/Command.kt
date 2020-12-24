package logan.api.command

import org.bukkit.command.CommandSender

interface Command<in T : CommandSender> {
    fun run(sender: T, args: Array<out String> = emptyArray(), data: Any? = null): Boolean
}