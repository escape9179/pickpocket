package logan.api.util

import org.bukkit.entity.Player

fun mutablePlayerListOf(vararg players: Player): MutablePlayerList {
    return MutablePlayerList().also { it.addAll(players) }
}

class MutablePlayerList : MutableList<Player> by mutableListOf() {
    override fun contains(element: Player) = any { it.uniqueId == element.uniqueId }
    override fun remove(element: Player) = removeIf { it.uniqueId == element.uniqueId }
}