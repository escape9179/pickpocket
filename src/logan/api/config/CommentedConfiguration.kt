package logan.api.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

open class CommentedConfiguration(private val file: File) {

    var configuration: YamlConfiguration

    init {
        file.createNewFile()
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    fun createKeyIfNoneExists(key: String, value: Any? = null) {
        configuration.setIfNotSet(key, value)
    }

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    fun save() = configuration.save(file)
}