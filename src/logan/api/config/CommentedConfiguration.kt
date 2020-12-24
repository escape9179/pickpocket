package logan.api.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileWriter

open class CommentedConfiguration(private val file: File) {

    private val commentPrefix = "# "
    private val commentToKeyMap = mutableMapOf<String, Array<out String>>()
    var configuration: YamlConfiguration

    init {
        file.createNewFile()
        configuration = YamlConfiguration.loadConfiguration(file)
    }

    fun createKeyIfNoneExists(key: String, value: Any?, comment: Array<out String>) {
        configuration.setIfNotSet(key, value)
        commentToKeyMap[key] = comment
    }

    fun createKeyIfNoneExists(key: String, value: Any?) = configuration.setIfNotSet(key, value)

    fun addCommentToKey(key: String, vararg comment: String) = commentToKeyMap.put(key, comment)

    fun save() {
        FileWriter(file).use {
            configuration.getKeys(true).forEach { key ->
                val value = configuration.get(key)
                val commentArray = commentToKeyMap[key]
                commentArray?.forEach { comment ->
                    it.append(commentPrefix)
                        .append(comment)
                        .append(System.lineSeparator())
                }
                it.append(key)
                    .append(": ")
                    .append(if (value is String) "\"$value\"" else value.toString())
                    .append(System.lineSeparator())
            }
        }
    }

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(file)
    }
}