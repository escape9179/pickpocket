package logan.pickpocket.config

import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.Profile
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ProfileConfiguration {
    val file = File(PickpocketPlugin.instance.dataFolder, "profiles.yml")
    var config = YamlConfiguration.loadConfiguration(file)

    init {
        if (!config.isConfigurationSection("profiles.default")) {
            config.createSection("profiles.default")
            config["profiles.default.cooldown"] = 10
            config["profiles.default.canUseFishingRod"] = false
            config["profiles.default.minigameRollRate"] = 20
            config["profiles.default.maxRummageCount"] = 5
            config["profiles.default.numberOfRummageItems"] = 4
            config["profiles.default.rummageDuration"] = 3
            config.save(file)
        }
    }

    fun loadProfiles(): List<Profile> {
        val profiles = mutableListOf<Profile>()
        val keys = config.getConfigurationSection("profiles")!!.getKeys(false)
        keys.forEach { profiles.add(loadProfile(it) ?: return@forEach) }
        return profiles
    }

    fun loadProfile(name: String): Profile? {
        val profileSection = config.getConfigurationSection("profiles.$name") ?: return null
        return Profile(name).apply {
            for (prop in properties)
                properties[prop.key] = profileSection.getString(prop.key)!!
        }
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun createProfile(name: String): Boolean {
        lateinit var profile: Profile
        config.run {
            if (isConfigurationSection( "profiles.$name")) return false
            profile = Profile(name)
            profile.properties.forEach { set("profiles.$name.${it.key}", it.value) }
            save(file)
        }
        return true
    }

    fun removeProfile(name: String): Boolean {
        return if (config.getConfigurationSection("profiles")!!.isConfigurationSection(name)) {
            config.set("profiles.$name", null)
            config.save(file)
            true
        } else false
    }

    fun saveProfile(profile: Profile): Boolean {
        val profileSection = config.getConfigurationSection("profiles.${profile.name}") ?: return false
        profile.properties.forEach { (k, v) -> profileSection[k] = v }
        config.save(file)
        return true
    }
}