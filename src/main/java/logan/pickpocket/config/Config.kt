package logan.pickpocket.config

import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Config {

    val file = File(PickpocketPlugin.instance.dataFolder, "config.yml")
    var config = YamlConfiguration.loadConfiguration(file)

    var isPickpocketingEnabled
        get() = config.getBoolean("pickpocketingEnabled")
        set(value) = config.set("pickpocketingEnabled", value)
    val moneyCanBeStolen = config.getBoolean("money.canBeStolen")
    val moneyPercentageToSteal = config.getDouble("money.percentageToSteal")
    var disabledItems = computeDisabledItems()
    var requiredClicksToPickpocket = config.getInt("requiredClicksToPickpocket")
    val statusOnInteract = config.getBoolean("statusOnInteract")
    val foreignTownTheft = config.getBoolean("foreignTownTheft")
    val sameTownTheft = config.getBoolean("sameTownTheft")
    val databaseEnabled = config.getBoolean("database.enabled")
    val databaseServer = config.getString("database.server")
    val databaseUsername = config.getString("database.username")
    val databasePassword = config.getString("database.password")

    private fun computeDisabledItems(): List<String> {
        val finalItems = mutableListOf<String>()
        for (item in config.getStringList("disabledItems")) {
            when (item.first()) {
                '*' -> finalItems.addAll(Material.values().map { it.name.lowercase() })
                '-' -> finalItems.remove(item.drop(1))
                else -> finalItems.add(item)
            }
        }
        return finalItems
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }
}