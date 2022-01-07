package logan.pickpocket.config

import de.leonhard.storage.Config
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Material

object PickpocketConfiguration {

    val config = Config("config.yml", PickpocketPlugin.instance.dataFolder.path)

    val moneyCanBeStolen = config.getBoolean("money.canBeStolen")
    val moneyPercentageToSteal = config.getDouble("money.percentageToSteal")
    var disabledItems = computeDisabledItems()
    val statusOnInteract = config.getBoolean("statusOnInteract")
    val statusOnLogin = config.getBoolean("statusOnLogin")
    val foreignTownTheft = config.getBoolean("foreignTownTheft")
    val sameTownTheft = config.getBoolean("sameTownTheft")
    val databaseEnabled = config.getBoolean("database.enabled")
    val databaseServer = config.getString("database.server")
    val databaseUser = config.getString("database.username")
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
}