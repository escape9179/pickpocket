package logan.api.command

import logan.api.util.hasNoPermission
import logan.api.util.sendColoredMessage
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KClass

class CommandDispatcher private constructor() {

    companion object : CommandExecutor {

        private val registeredCommands = mutableSetOf<BasicCommand<*>>()

        fun registerCommand(command: BasicCommand<*>) = registeredCommands.add(command)

        override fun onCommand(
            sender: CommandSender,
            command: Command,
            label: String,
            args: Array<out String>
        ): Boolean {

            val foundCommand = searchForSubCommandRecursively(label, args) ?: return true

            if (sender.hasNoPermission(foundCommand.first.permissionNode)) {
                sender.sendColoredMessage("&cNo permission.")
                return true
            }

            val commandTarget = if (sender is Player) SenderTarget.PLAYER else SenderTarget.CONSOLE

            if (!isValidTarget(commandTarget, foundCommand.first.target)) {
                sender.sendMessage("${ChatColor.RED}You cannot perform this command as ${sender::class.simpleName}")
                return true
            }

            /* Subtract 1 from the arg size to ignore sub-command name arg. */
            if (!isValidArgLength(foundCommand.second.size, foundCommand.first.argRange)) {
                foundCommand.first.usage?.let { sender.sendMessage(it) }
                return true
            }

            if (!isCorrectArgTypeList(foundCommand.second, foundCommand.first.argTypes)) {
                foundCommand.first.usage?.let { sender.sendMessage(it) }
                return true
            }

            return when (foundCommand.first.target) {
                SenderTarget.PLAYER -> (foundCommand.first as BasicCommand<Player>).run(
                    sender as Player,
                    foundCommand.second
                )
                else -> (foundCommand.first as BasicCommand<CommandSender>).run(sender, foundCommand.second)
            }
        }

        private fun searchForSubCommandRecursively(
            commandName: String,
            args: Array<out String>
        ): Pair<BasicCommand<*>, Array<out String>>? {

            val foundCommand = registeredCommands.find { it.name == commandName } ?: return null

            /* If there are no arguments provided to the main command, then the user is probably
             expecting help with the command, but this can be left up to the developer.
             */
            if (args.isEmpty()) return foundCommand to args

            return searchForSubCommandRecursively(args[0], args.sliceArray(1..args.lastIndex)) ?: (foundCommand to args)
        }

        private fun isValidTarget(receivedCommandTarget: SenderTarget, foundCommandTarget: SenderTarget) =
            !((receivedCommandTarget != foundCommandTarget) && (foundCommandTarget != SenderTarget.BOTH))

        private fun isValidArgLength(receivedArgNum: Int, requiredArgRange: IntRange) =
            (receivedArgNum >= requiredArgRange.first) && (receivedArgNum <= requiredArgRange.last)

        //TODO Have function return casted arguments
        private fun isCorrectArgTypeList(receivedArgs: Array<out String>, requiredArgTypes: List<KClass<*>>): Boolean {
            for (i in receivedArgs.indices) {
                when (requiredArgTypes[i]) {
                    Int::class -> receivedArgs[i].toIntOrNull() ?: return false
                    Double::class -> receivedArgs[i].toDoubleOrNull() ?: return false
                    String::class -> receivedArgs[i]
                    else -> return false
                }
            }
            return true
        }
    }
}