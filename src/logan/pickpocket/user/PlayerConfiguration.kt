package logan.pickpocket.user

import logan.api.config.BasicConfiguration
import logan.api.config.setIfNotSet
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * Created by Tre on 1/18/2016.
 */
class PlayerConfiguration(directory: String, fileName: String) : BasicConfiguration {

    override var file: File = File(directory, fileName)
    override var configuration: YamlConfiguration = YamlConfiguration.loadConfiguration(file)

    var isAdmin
        get() = configuration.getBoolean("admin")
        set(value) = configuration.set("admin", value)
    var isBypassing
        get() = configuration.getBoolean("bypass")
        set(value) = configuration.set("bypass", value)
    var isExempt
        get() = configuration.getBoolean("exempt")
        set(value) { configuration.set("exempt", value) }
    var stealCount
        get() = configuration.getInt("steals")
        set(value) = configuration.set("steals", value)
    var isParticipating
        get() = configuration.getBoolean("participating")
        set(value) = configuration.set("participating", value)
    var thiefProfile
        get() = configuration.getString("thiefProfile")
        set(value) { configuration.set("thiefProfile", value) }

    init {
        configuration.setIfNotSet("admin", false)
        configuration.setIfNotSet("bypass", false)
        configuration.setIfNotSet("exempt", false)
        configuration.setIfNotSet("steals", 0)
        configuration.setIfNotSet("participating", false)
        configuration.setIfNotSet("thiefProfile", "default")
        save()
    }
}