package logan.api.command

import org.bukkit.command.CommandSender
import java.util.Arrays
import kotlin.reflect.KClass

abstract class BasicCommand<T : CommandSender>(
    val name: String,
    val permissionNode: String,
    val argRange: IntRange = 0..0,
    val aliases: Array<String> = emptyArray(),
    val target: SenderTarget,
    val parentCommand: String? = null,
    val argTypes: List<KClass<*>> = emptyList(),
    val usage: String? = null,
) : Command<T> {
    override fun equals(other: Any?) =
        (super.equals(other)) && (if (other is BasicCommand<*>) other.name == name else false)
}