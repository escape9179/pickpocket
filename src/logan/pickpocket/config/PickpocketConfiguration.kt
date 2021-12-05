package logan.pickpocket.config

import logan.api.config.CommentedConfiguration
import logan.pickpocket.main.PickpocketPlugin.Companion.instance
import java.io.File

class PickpocketConfiguration : CommentedConfiguration(File(instance.dataFolder, "config.yml")) {
    fun create() {
        createKeyIfNoneExists(loseMoney, false)
        createKeyIfNoneExists(moneyLost, 0.025)
        createKeyIfNoneExists(minigameRollRateKey, 20)
        createKeyIfNoneExists(cooldownTimeKey, 10)
        createKeyIfNoneExists(pickpocketToggleKey, true)
        createKeyIfNoneExists(statusOnInteractKey, true)
        createKeyIfNoneExists(statusOnLoginKey, true)
        createKeyIfNoneExists(disabledItemsKey, listOf("cake", "shulker_box", "bundle"))
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
    val minigameRollRate: Int
        get() = configuration.getInt(minigameRollRateKey)
    val disabledItems: List<String>
        get() = configuration.getStringList(disabledItemsKey)
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
        private const val sameTownTheftKey = "same-town-theft"
        private const val databaseEnabledKey = "database.enable"
        private const val databaseServerKey = "database.server"
        private const val databaseUserKey = "database.user"
        private const val databasePasswordKey = "database.password"
    }
}