package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
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
        /pickpocket profile create <thief|victim> <name>
        /pickpocket profile remove <thief|victim> <name>
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
)

class ProfileCreateCommand : BasicCommand<Player>(
    "create",
    2..2,
    listOf(String::class, String::class),
    "profile",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile.create",
    """
        Usage:
        /pickpocket profile create <thief|victim> <name>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        when (args[0].lowercase()) {
            "thief" -> {
                val result = ThiefProfile(args[1]).save()
                sender.sendMessage(
                    if (result) "Successfully created thief profile ${args[1]}."
                    else "Error: Profile ${args[1]} already exists."
                )
            }
            "victim" -> {
                val result = VictimProfile(args[1]).save()
                sender.sendMessage(
                    if (result) "Successfully created victim profile ${args[1]}."
                    else "Error: Profile ${args[1]} already exists."
                )
            }
        }
        return true
    }
}

interface Profile {
    val name: String
    val type: ProfileType
    val properties: MutableMap<String, out Any>
    fun save(file: File = File(PickpocketPlugin.instance.dataFolder, "profiles.yml")): Boolean {
        file.createNewFile()
        YamlConfiguration.loadConfiguration(file).run {
            if (isConfigurationSection(this@Profile.type.friendlyName + "." + this@Profile.name)) return false
            properties.forEach { set("${type.friendlyName}.${this@Profile.name}.${it.key}", it.value) }
            save(file)
        }
        return true
    }
}

enum class ProfileType {
    THIEF, VICTIM;

    val friendlyName = name.lowercase()
}

class ThiefProfile(override val name: String) : Profile {
    override val type = ProfileType.THIEF
    override val properties = mutableMapOf(
        "cooldown" to 10,
        "canFishingRod" to false,
        "minigameRollRate" to 20,
        "maxRummages" to 5,
        "rummageDuration" to 3,
        "numRummageItems" to 4
    )
}

class VictimProfile(override val name: String) : Profile {
    override val type = ProfileType.VICTIM
    override val properties = mutableMapOf<String, Any>()
}