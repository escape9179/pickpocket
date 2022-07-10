package logan.api.config

import org.bukkit.configuration.file.YamlConfiguration

fun YamlConfiguration.isNotSet(key: String) = !this.isSet(key)

fun YamlConfiguration.setIfNotSet(key: String, value: Any?) {
    if (this.isNotSet(key)) this.set(key, value)
}