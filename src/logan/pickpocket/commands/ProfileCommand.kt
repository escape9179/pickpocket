package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.gui.InventoryMenu
import logan.api.gui.MenuItem
import logan.api.gui.PlayerInventoryMenu
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

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
        /pickpocket profile victim create <name>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        when (args[0].lowercase()) {
            "victim" -> sender.openVictimProfileMenu(args[2])
            "thief" -> sender.openThiefProfileMenu(args[2])
        }
        return true
    }
}

fun Player.openVictimProfileMenu(name: String) {
    val menu = VictimProfileMenu(name)
    menu.show(this)
}

fun Player.openThiefProfileMenu(name: String) {
    val menu = ThiefProfileMenu(name)
    menu.show(this)
}

data class ThiefProfile(
    var name: String
)

class ThiefProfileMenu(
    private val profileName: String,
    private val menu: InventoryMenu = PlayerInventoryMenu("Configuring Thief $profileName", 4)
) : InventoryMenu by menu {
    init {
        val thiefProfiles = loadThiefProfiles("plugins/Pickpocket/thief_profiles.yml")
        thiefProfiles.forEach { PickpocketPlugin.log("Loading thief ${it.name}") }
        val createProfileMenuItem = MenuItem("Create thief profile", ItemStack(Material.BOOK))
        createProfileMenuItem.addListener {
            File("plugins/Pickpocket/thief_profiles.yml").createNewFile()
        }
        menu.addItem(bottomLeft, createProfileMenuItem)
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
    private val profileName: String,
    private val menu: InventoryMenu = PlayerInventoryMenu("Configuring Victim $profileName", 4)
) : InventoryMenu by menu