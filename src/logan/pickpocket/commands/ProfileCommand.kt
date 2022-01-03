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
            ProfileType.THIEF.friendlyName -> {
                val result = ThiefProfile(args[1]).save()
                sender.sendMessage(
                    if (result) "Successfully created thief profile ${args[1]}."
                    else "Error: Profile ${args[1]} already exists."
                )
            }
            ProfileType.VICTIM.friendlyName -> {
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

class ProfileEditCommand : BasicCommand<Player>(
    "edit",
    4..4,
    listOf(String::class, String::class, String::class, String::class),
    "profile",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile.edit",
    """
        Usage:
        /pickpocket profile edit <thief|victim> <name> <property> <value>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        when (args[0]) {
            ProfileType.THIEF.friendlyName -> {
                TODO()
            }
            ProfileType.VICTIM.friendlyName -> {
                TODO()
            }
        }
        return true
    }
}

class ProfileRemoveCommand : BasicCommand<Player>(
    "remove",
    2..2,
    listOf(String::class, String::class),
    "profile",
    SenderTarget.PLAYER,
    "pickpocket.admin.profile.remove",
    """
        Usage:
        /pickpocket profile remove <thief|victim> <name>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        TODO()
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
        "canUseFishingRod" to false,
        "minigameRollRate" to 20,
        "maxRummageCount" to 5,
        "numberOfRummageItems" to 4,
        "rummageDuration" to 3,
    )

    val cooldown
        get() = properties["cooldown"] as Int
    val canUseFishingRod
        get() = properties["canUseFishingRod"] as Boolean
    val maxRummageCount
        get() = properties["maxRummageCount"] as Int
    val minigameRollRate
        get() = properties["minigameRollRate"] as Long
    val numberOfRummageItems
        get() = properties["numberOfRummageItems"] as Int
    val rummageDuration
        get() = properties["rummageDuration"] as Int
}

class VictimProfile(override val name: String) : Profile {
    override val type = ProfileType.VICTIM
    override val properties = mutableMapOf<String, Any>()
}