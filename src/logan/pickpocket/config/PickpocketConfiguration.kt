package logan.pickpocket.config

import logan.api.config.CommentedConfiguration
import logan.pickpocket.main.PickpocketPlugin.Companion.instance
import java.io.File
import java.util.*

class PickpocketConfiguration : CommentedConfiguration(File(instance.dataFolder, "config.yml")) {
    fun create() {
        createKeyIfNoneExists(loseMoney, false)
        createKeyIfNoneExists(moneyLost, 0.025)
        createKeyIfNoneExists(minigameRollRateKey, 20)
        createKeyIfNoneExists(cooldownTimeKey, 10)
        createKeyIfNoneExists(pickpocketToggleKey, true)
        createKeyIfNoneExists(statusOnInteractKey, true)
        createKeyIfNoneExists(statusOnLoginKey, true)
        createKeyIfNoneExists(disabledItemsKey, Arrays.asList("cake", "shulker_box", "bundle"))
        createKeyIfNoneExists(foreignTownTheftKey, false)
        createKeyIfNoneExists(sameTownTheftKey, false)
        addCommentToKey(loseMoney, "Whether or not predators should take money from", "victims when pick-pocketing.")
        addCommentToKey(
            moneyLost,
            "The percentage of money taken by the predator after",
            "successfully pick-pocketing another player."
        )
        addCommentToKey(
            minigameRollRateKey,
            "The time in ticks a user has before the",
            "mini-game inventory slots are randomized again."
        )
        addCommentToKey(
            cooldownTimeKey,
            "The time the player must wait in seconds",
            "between pick-pocketing attempts.",
            "An attempt is when a player successfully",
            "pick-pockets another player."
        )
        addCommentToKey(
            pickpocketToggleKey,
            "Allow players to disable pick-pocketing",
            "for themselves. This will also disallow others",
            "from pick-pocketing them."
        )
        addCommentToKey(
            statusOnInteractKey,
            "Whether or not to show a players the",
            "pick-pocket status message when they attempt",
            "to pick-pocket another player whilst they, or the",
            "victim has pick-pocketing disabled."
        )
        addCommentToKey(statusOnLoginKey, "Whether or not to show a players pick-pocket status when logging in.")
        addCommentToKey(
            disabledItemsKey,
            "Items that can't be stolen and therefore, won't show",
            "up in the rummage GUI. A list of Minecraft IDs can be found",
            "at www.deadmap.com/idlist"
        )
        addCommentToKey(foreignTownTheftKey, "Whether players should be able to steal from people in their own town.")
        addCommentToKey(sameTownTheftKey, "Whether players should be able to steal from their own town-folk")
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
    }
}