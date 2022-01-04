package logan.pickpocket.config

import logan.api.config.BasicConfiguration
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class PickpocketConfiguration(override var file: File) : BasicConfiguration {
    override var configuration: YamlConfiguration = YamlConfiguration.loadConfiguration(file)
    fun create() {
        createKeyIfNoneExists(loseMoney, false)
        createKeyIfNoneExists(moneyLost, 0.025)
        createKeyIfNoneExists(minigameRollRateKey, 20)
        createKeyIfNoneExists(cooldownTimeKey, 10)
        createKeyIfNoneExists(pickpocketToggleKey, true)
        createKeyIfNoneExists(statusOnInteractKey, true)
        createKeyIfNoneExists(statusOnLoginKey, true)
        createKeyIfNoneExists(disabledItemsKey, listOf("shulker_box", "bundle"))
        createKeyIfNoneExists(fishingRodEnabledKey, false)
        createKeyIfNoneExists(foreignTownTheftKey, false)
        createKeyIfNoneExists(sameTownTheftKey, false)
        createKeyIfNoneExists(databaseEnabledKey, false)
        createKeyIfNoneExists(databaseServerKey, "")
        createKeyIfNoneExists(databaseUserKey, "")
        createKeyIfNoneExists(databasePasswordKey, "")
        save()
    }

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

    companion object {
        private const val loseMoney = "lose-money-on-pickpocket"
        private const val moneyLost = "pickpocket-money-lost"
        private const val minigameRollRateKey = "minigame-roll-rate"
        private const val cooldownTimeKey = "cooldown-time"
        private const val pickpocketToggleKey = "allow-pickpocket-toggling"
        private const val statusOnInteractKey = "show-status-on-interact"
        private const val statusOnLoginKey = "show-status-on-login"
        private const val disabledItemsKey = "disabled-items"
        private const val foreignTownTheftKey = "foreign-town-theft"
        private const val fishingRodEnabledKey = "fishing-rod-pickpocketing"
        private const val sameTownTheftKey = "same-town-theft"
        private const val databaseEnabledKey = "database.enable"
        private const val databaseServerKey = "database.server"
        private const val databaseUserKey = "database.user"
        private const val databasePasswordKey = "database.password"
    }
}