package logan.api.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

interface BasicConfiguration {
    var file: File
    var configuration: YamlConfiguration

    fun createKeyIfNoneExists(key: String, value: Any? = null) {
        configuration.setIfNotSet(key, value)
    }

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    operator fun <T> set(key: String, value: T) {
        configuration.set(key, value)
    }

    fun save() = configuration.save(file)
}