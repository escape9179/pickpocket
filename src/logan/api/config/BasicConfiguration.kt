package logan.api.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

interface BasicConfiguration {

    var configuration: YamlConfiguration
    var file: File

    fun createKeyIfNoneExists(key: String, value: Any? = null) {
        configuration.setIfNotSet(key, value)
    }

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    fun save() = configuration.save(file)
}