package logan.api.command

import org.bukkit.command.CommandSender
import kotlin.reflect.KClass

abstract class BasicCommand<T : CommandSender>(
    val name: String,
    val argRange: IntRange = 0..0,
    val argTypes: List<KClass<*>> = emptyList(),
    val parentCommand: String? = null,
    val target: SenderTarget,
    val permissionNode: String,
    val usage: String? = null,
    vararg val aliases: String = emptyArray()
) : Command<T> {
    override fun equals(other: Any?) =
        (super.equals(other)) && (if (other is BasicCommand<*>) other.name == name else false)
}