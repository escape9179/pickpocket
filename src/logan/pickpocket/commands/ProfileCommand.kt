package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.gui.InventoryMenu
import logan.api.gui.PlayerInventoryMenu
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class ProfileCommand : BasicCommand<Player>(
    "profile",
    1..1,
    listOf(String::class, String::class, String::class),
    "pickpocket",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile",
    """
        Usage:
        /pickpocket profile thief
        /pickpocket profile victim
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        when (args[0].lowercase()) {
            "victim" -> sender.openVictimProfileMenu()
            "thief" -> sender.openThiefProfileMenu()
        }
        return true
    }
}

fun Player.openVictimProfileMenu() {
    val menu = VictimProfileMenu()
    menu.show(this)
}

fun Player.openThiefProfileMenu() {
    val menu = ThiefProfileMenu()
    menu.show(this)
}

data class ThiefProfile(
    var name: String
)

class ThiefProfileMenu(
    private val menu: InventoryMenu = PlayerInventoryMenu("Thief profiles", 4)
) : InventoryMenu by menu {
    init {
        val thiefProfiles = loadThiefProfiles("plugins/Pickpocket/thief_profiles.yml")
        thiefProfiles.forEach { PickpocketPlugin.log("Loading thief ${it.name}") }
    }

    private fun extractThiefProfileFromConfigurationSection(section: ConfigurationSection): ThiefProfile {
        val name = section.getString("name")!!
        return ThiefProfile(name)
    }

    private fun loadThiefProfiles(path: String): List<ThiefProfile> {
        val config = YamlConfiguration.loadConfiguration(File(path))
        val thiefProfiles = mutableListOf<ThiefProfile>()
        config.getKeys(false).forEach {
            val section = config.getConfigurationSection(it)!!
            thiefProfiles.add(extractThiefProfileFromConfigurationSection(section))
        }
        return thiefProfiles
    }
}

class VictimProfileMenu(
    private val menu: InventoryMenu = PlayerInventoryMenu("Victim profiles", 4)
) : InventoryMenu by menu