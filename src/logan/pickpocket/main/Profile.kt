package logan.pickpocket.main

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

interface Profile {
    val name: String
    val type: ProfileType
    val properties: MutableMap<String, Any>

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