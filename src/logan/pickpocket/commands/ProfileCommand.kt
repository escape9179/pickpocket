package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.gui.InventoryMenu
import logan.api.gui.PlayerInventoryMenu
import org.bukkit.entity.Player

class ProfileCommand : BasicCommand<Player>(
    "profile",
    3..3,
    listOf(String::class, String::class, String::class),
    "pickpocket",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile",
    """
        Usage:
        /pickpocket profile thief create <name>
        /pickpocket profile player create <name>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        sender.openThiefProfileMenu(args[2])
        return true
    }
}

fun Player.openThiefProfileMenu(name: String) {
    val menu = ThiefProfileMenu(name)
    menu.show(this)
}

class ThiefProfileMenu(
    private val profileName: String,
    private val menu: InventoryMenu = PlayerInventoryMenu(profileName, 4)
) : InventoryMenu by menu {

}