package logan.pickpocket.config

import de.leonhard.storage.Config
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Material

object PickpocketConfiguration {

    val config = Config("config.yml", PickpocketPlugin.instance.dataFolder.path)

    val isMoneyLostEnabled: Boolean
        get() = configuration.getBoolean(loseMoney)
    val moneyLostPercentage: Double
        get() = configuration.getDouble(moneyLost)
    var disabledItems: List<String> = computeDisabledItems()
    val isShowStatusOnInteractEnabled: Boolean
        get() = configuration.getBoolean(statusOnInteractKey)
    val isShowStatusOnLoginEnabled: Boolean
        get() = configuration.getBoolean(statusOnLoginKey)
    val cooldownTime: Int
        get() = configuration.getInt(cooldownTimeKey)
    val isForeignTownTheftEnabled: Boolean
        get() = configuration.getBoolean(foreignTownTheftKey)
    val isSameTownTheftEnabled: Boolean
        get() = configuration.getBoolean(sameTownTheftKey)
    val databaseEnabled
        get() = configuration.getBoolean(databaseEnabledKey)
    val databaseServer
        get() = configuration.getString(databaseServerKey)
    val databaseUser
        get() = configuration.getString(databaseUserKey)
    val databasePassword
        get() = configuration.getString(databasePasswordKey)

    private fun computeDisabledItems(): List<String> {
        val finalItems = mutableListOf<String>()
        for (item in configuration.getStringList(disabledItemsKey)) {
            when (item.first()) {
                '*' -> finalItems.addAll(Material.values().map { it.name.lowercase() })
                '-' -> finalItems.remove(item.drop(1))
                else -> finalItems.add(item)
            }
        }
        return finalItems
    }

    override fun reload() {
        super.reload()
        disabledItems = computeDisabledItems()
    }
}