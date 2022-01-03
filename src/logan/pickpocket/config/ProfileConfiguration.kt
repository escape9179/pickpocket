package logan.pickpocket.config

import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.ThiefProfile
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ProfileConfiguration {
    val file = File(PickpocketPlugin.instance.dataFolder, "profiles.yml")
    val config = YamlConfiguration.loadConfiguration(file)

    init {
        if (!config.isConfigurationSection("thiefProfiles.default")) {
            config.createSection("thiefProfiles.default")
            config["thiefProfiles.default.cooldown"] = 10
            config["thiefProfiles.default.canUseFishingRod"] = false
            config["thiefProfiles.default.minigameRollRate"] = 20
            config["thiefProfiles.default.maxRummageCount"] = 5
            config["thiefProfiles.default.numberOfRummageItems"] = 4
            config["thiefProfiles.default.rummageDuration"] = 3
            config.save(file)
        }
    }

    fun loadThiefProfiles(): List<ThiefProfile> {
        val profiles = mutableListOf<ThiefProfile>()
        val keys = config.getConfigurationSection("thiefProfiles")!!.getKeys(false)
        keys.forEach { profiles.add(loadThiefProfile(it)) }
        return profiles
    }

    private fun loadThiefProfile(name: String): ThiefProfile {

        TODO()
    }

    fun removeThiefProfile(name: String): Boolean {
        return if (config.getConfigurationSection("thiefProfiles")!!.isConfigurationSection(name)) {
            config.set("thiefProfiles.$name", null)
            config.save(file)
            true
        } else false
    }
}