package logan.pickpocket.config

import logan.api.config.BasicConfiguration
import logan.api.util.ColorUtils
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

/**
 * Represents a configuration file used
 * for setting message preferences for the plugin.
 */
object MessageConfiguration : BasicConfiguration {
    override var file: File = File(PickpocketPlugin.instance.dataFolder, "messages.yml")
    override var configuration: YamlConfiguration = YamlConfiguration.loadConfiguration(file)
    val ADMIN_STATUS_CHANGE_KEY = "admin-status-change"
    val BYPASS_STATUS_CHANGE_KEY = "bypass-status-change"
    val BYPASS_STATUS_CHANGE_OTHER_KEY = "bypass-status-other-change"
    val EXEMPT_STATUS_CHANGE_KEY = "exempt-status-change"
    val EXEMPT_STATUS_CHANGE_OTHER_KEY = "exempt-status-change-other"
    val RELOAD_NOTIFICATION_KEY = "reload-notification"
    val PLAYER_NOT_FOUND_KEY = "player-not-found"
    val PLAYER_NOT_ACCESSIBLE_KEY = "player-not-accessible"
    val PICKPOCKET_DISABLED_KEY = "pickpocket-disabled"
    val PICKPOCKET_DISABLED_OTHER_KEY = "pickpocket-disabled-other"
    val PICKPOCKET_TOGGLE_ON_KEY = "pickpocket-toggle-on"
    val PICKPOCKET_TOGGLE_OFF_KEY = "pickpocket-toggle-off"
    val PERSON_CANT_BE_STOLEN_FROM_KEY = "person-cant-be-stolen-from"
    val PICKPOCKET_REGION_DISALLOW_KEY = "pickpocket-region-disallow"
    val PICKPOCKET_UNSUCCESSFUL_KEY = "pickpocket-unsuccessful"
    val PICKPOCKET_SUCCESSFUL_KEY = "pickpocket-successful"
    val PARTICIPATING_TRUE_NOTIFICATION_KEY = "participating-true-notification"
    val PARTICIPATING_FALSE_NOTIFICATION_KEY = "participating-false-notification"
    val PICKPOCKET_ON_MOVE_WARNING_KEY = "pickpocket-on-move-warning"
    val PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY = "pickpocket-on-move-other-warning"
    val PICKPOCKET_VICTIM_WARNING_KEY = "pickpocket-victim-warning"
    val PICKPOCKET_NOTICED_WARNING_KEY = "pickpocket-noticed-warning"
    val COOLDOWN_NOTICE_KEY = "cooldown-notice"
    val NO_MONEY_RECEIVED = "no-money-received"
    val MONEY_AMOUNT_RECEIVED = "money-amount-received"
    val PICKPOCKET_SUCCESS_ADMIN_NOTIFICATION = "admin-notify-success"
    val PICKPOCKET_FAILURE_ADMIN_NOTIFICATION = "admin-notify-failure"
    val PLAYER_STEAL_FROM_AFK = "player-attempt-steal-from-afk"
    val PLAYER_STEAL_WHILE_AFK = "player-attempt-steal-while-afk"
    fun create() {
        createKeyIfNoneExists(ADMIN_STATUS_CHANGE_KEY, "&7Pickpocket Admin status set to %value%.")
        createKeyIfNoneExists(BYPASS_STATUS_CHANGE_KEY, "&7Your bypass status has been changed to %value%.")
        createKeyIfNoneExists(BYPASS_STATUS_CHANGE_OTHER_KEY, "&7Changed %player%'s bypass status to %value%.")
        createKeyIfNoneExists(EXEMPT_STATUS_CHANGE_KEY, "&7Your exempt status has been changed to %value%.")
        createKeyIfNoneExists(EXEMPT_STATUS_CHANGE_OTHER_KEY, "&7Changed %player's exempt status to %value%.")
        createKeyIfNoneExists(RELOAD_NOTIFICATION_KEY, "&aReloaded Pickpocket configuration.")
        createKeyIfNoneExists(PLAYER_NOT_FOUND_KEY, "&cPlayer not found.")
        createKeyIfNoneExists(PLAYER_NOT_ACCESSIBLE_KEY, "&cThat player is not accessible.")
        createKeyIfNoneExists(PICKPOCKET_DISABLED_KEY, "&cYou have pick-pocketing disabled.")
        createKeyIfNoneExists(PICKPOCKET_DISABLED_OTHER_KEY, "&cThat player has pick-pocketing disabled.")
        createKeyIfNoneExists(PICKPOCKET_TOGGLE_ON_KEY, "&7Pick-pocketing is now enabled.")
        createKeyIfNoneExists(PICKPOCKET_TOGGLE_OFF_KEY, "&7Pick-pocketing is now disabled.")
        createKeyIfNoneExists(PERSON_CANT_BE_STOLEN_FROM_KEY, "&7This person cannot be stolen from.")
        createKeyIfNoneExists(PICKPOCKET_REGION_DISALLOW_KEY, "&cPick-pocketing is disabled in this region.")
        createKeyIfNoneExists(PICKPOCKET_UNSUCCESSFUL_KEY, "&cPickpocket attempt unsuccessful.")
        createKeyIfNoneExists(PICKPOCKET_SUCCESSFUL_KEY, "&aPickpocket attempt successful.")
        createKeyIfNoneExists(
            PARTICIPATING_TRUE_NOTIFICATION_KEY,
            "&7You are currently participating in pick-pocketing."
        )
        createKeyIfNoneExists(
            PARTICIPATING_FALSE_NOTIFICATION_KEY,
            "&7You are currently not participating in pick-pocketing."
        )
        createKeyIfNoneExists(PICKPOCKET_ON_MOVE_WARNING_KEY, "&cYou cannot move whilst pick-pocketing.")
        createKeyIfNoneExists(PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY, "&cThe player moved.")
        createKeyIfNoneExists(PICKPOCKET_VICTIM_WARNING_KEY, "&cYou feel something touch your side.")
        createKeyIfNoneExists(PICKPOCKET_NOTICED_WARNING_KEY, "&cYou've been noticed.")
        createKeyIfNoneExists(
            COOLDOWN_NOTICE_KEY,
            "&cYou must wait %value% seconds before attempting another pickpocket."
        )
        createKeyIfNoneExists(NO_MONEY_RECEIVED, "&cYou received no money.")
        createKeyIfNoneExists(MONEY_AMOUNT_RECEIVED, "&aYou received $%value%.")
        createKeyIfNoneExists(
            PICKPOCKET_SUCCESS_ADMIN_NOTIFICATION,
            "&a%player% succeeded in pick-pocketing %victim%."
        )
        createKeyIfNoneExists(
            PICKPOCKET_FAILURE_ADMIN_NOTIFICATION,
            "&c%player% failed in pick-pocketing %victim%."
        )
        createKeyIfNoneExists(PLAYER_STEAL_FROM_AFK, "&cThat player is AFK.")
        createKeyIfNoneExists(PLAYER_STEAL_WHILE_AFK, "&cYou cannot pick-pocket while AFK.")
        save()
    }

    private fun getMessage(key: String): String {
        val keyValue = configuration.getString(key)!!
        return ColorUtils.colorize(keyValue)
    }

    private fun getMessage(key: String, value: String): String {
        val keyValue = configuration.getString(key)!!
        val parsedValue = keyValue.replace("%value%", value)
        return ColorUtils.colorize(parsedValue)
    }

    private fun getMessage(key: String, player: Player): String {
        val keyValue = configuration.getString(key)!!
        val parsedValue = keyValue.replace("%player%", player.name)
        return ColorUtils.colorize(parsedValue)
    }

    private fun getMessage(key: String, player: Player, value: String): String {
        val keyValue = configuration.getString(key)!!
        val replacedValue = keyValue.replace("%player%", player.name)
        val replacedPlayer = replacedValue.replace("%value%", value)
        return ColorUtils.colorize(replacedPlayer)
    }

    private fun getMessage(key: String, player: Player, victim: Player): String {
        val keyValue = configuration.getString(key)!!
        val replacedPlayer = keyValue.replace("%player%", player.name)
        val replacedVictim = replacedPlayer.replace("%victim%", victim.name)
        return ColorUtils.colorize(replacedVictim)
    }

    fun getAdminStatusChangeMessage(value: Any): String {
        return getMessage(ADMIN_STATUS_CHANGE_KEY, value.toString())
    }

    fun getBypassStatusChangeMessage(value: Any): String {
        return getMessage(BYPASS_STATUS_CHANGE_KEY, value.toString())
    }

    fun getBypassStatusChangeOtherMessage(player: Player, value: Any): String {
        return getMessage(BYPASS_STATUS_CHANGE_OTHER_KEY, player, value.toString())
    }

    fun getExemptStatusChangeMessage(value: Any): String {
        return getMessage(EXEMPT_STATUS_CHANGE_KEY, value.toString())
    }

    fun getExemptStatusChangeOtherMessage(player: Player, value: Any): String {
        return getMessage(EXEMPT_STATUS_CHANGE_OTHER_KEY, player, value.toString())
    }

    val reloadNotificationMessage: String
        get() = getMessage(RELOAD_NOTIFICATION_KEY)
    val playerNotFoundMessage: String
        get() = getMessage(PLAYER_NOT_FOUND_KEY)
    val playerNotAccessibleMessage: String
        get() = getMessage(PLAYER_NOT_ACCESSIBLE_KEY)
    val pickpocketDisabledMessage: String
        get() = getMessage(PICKPOCKET_DISABLED_KEY)
    val pickpocketDisabledOtherMessage: String
        get() = getMessage(PICKPOCKET_DISABLED_OTHER_KEY)
    val pickpocketToggleOnMessage: String
        get() = getMessage(PICKPOCKET_TOGGLE_ON_KEY)
    val pickpocketToggleOffMessage: String
        get() = getMessage(PICKPOCKET_TOGGLE_OFF_KEY)
    val personCantBeStolenFromMessage: String
        get() = getMessage(PERSON_CANT_BE_STOLEN_FROM_KEY)
    val pickpocketRegionDisallowMessage: String
        get() = getMessage(PICKPOCKET_REGION_DISALLOW_KEY)
    val pickpocketUnsuccessfulMessage: String
        get() = getMessage(PICKPOCKET_UNSUCCESSFUL_KEY)
    val pickpocketSuccessfulMessage: String
        get() = getMessage(PICKPOCKET_SUCCESSFUL_KEY)
    val participatingTrueNotificationMessage: String
        get() = getMessage(PARTICIPATING_TRUE_NOTIFICATION_KEY)
    val participatingFalseNotificationMessage: String
        get() = getMessage(PARTICIPATING_FALSE_NOTIFICATION_KEY)
    val pickpocketOnMoveWarningMessage: String
        get() = getMessage(PICKPOCKET_ON_MOVE_WARNING_KEY)
    val pickpocketOnMoveOtherWarningMessage: String
        get() = getMessage(PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY)
    val pickpocketVictimWarningMessage: String
        get() = getMessage(PICKPOCKET_VICTIM_WARNING_KEY)
    val pickpocketNoticedWarningMessage: String
        get() = getMessage(PICKPOCKET_NOTICED_WARNING_KEY)

    fun getCooldownNoticeMessage(value: Any): String {
        return getMessage(COOLDOWN_NOTICE_KEY, value.toString())
    }

    val noMoneyReceivedMessage: String
        get() = getMessage(NO_MONEY_RECEIVED)

    fun getMoneyAmountReceivedMessage(value: Any): String {
        return getMessage(MONEY_AMOUNT_RECEIVED, value.toString())
    }

    fun getPickpocketSuccessAdminNotificationMessage(player: Player, victim: Player): String {
        return getMessage(PICKPOCKET_SUCCESS_ADMIN_NOTIFICATION, player, victim)
    }

    fun getPickpocketFailureAdminNotification(player: Player, victim: Player): String {
        return getMessage(PICKPOCKET_FAILURE_ADMIN_NOTIFICATION, player, victim)
    }

    val playerStealFromAfkMessage: String
        get() = getMessage(PLAYER_STEAL_FROM_AFK)
    val playerStealWhileAfk: String
        get() = getMessage(PLAYER_STEAL_WHILE_AFK)
}