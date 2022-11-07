package logan.pickpocket.config

import logan.api.util.ColorUtils
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

/**
 * Configuration file used for setting message preferences for the plugin.
 */
object MessageConfiguration {

    val file = File(PickpocketPlugin.instance.dataFolder, "messages.yml")
    private var config = YamlConfiguration.loadConfiguration(file)

    private fun getMessage(key: String): String {
        val keyValue = config.getString(key)!!
        return ColorUtils.colorize(keyValue)
    }

    private fun getMessage(key: String, vararg values: String): String {
        var keyValue = config.getString(key)!!
        values.forEach { keyValue = keyValue.replaceFirst("%value%", it) }
        return ColorUtils.colorize(keyValue)
    }

    private fun getMessage(key: String, player: Player): String {
        val keyValue = config.getString(key)!!
        val parsedValue = keyValue.replace("%player%", player.name)
        return ColorUtils.colorize(parsedValue)
    }

    private fun getMessage(key: String, player: Player, value: String): String {
        val keyValue = config.getString(key)!!
        val replacedValue = keyValue.replace("%player%", player.name)
        val replacedPlayer = replacedValue.replace("%value%", value)
        return ColorUtils.colorize(replacedPlayer)
    }

    private fun getMessage(key: String, player: Player, victim: Player): String {
        val keyValue = config.getString(key)!!
        val replacedPlayer = keyValue.replace("%player%", player.name)
        val replacedVictim = replacedPlayer.replace("%victim%", victim.name)
        return ColorUtils.colorize(replacedVictim)
    }

    val reloadNotificationMessage: String
        get() = getMessage("pluginReload")
    val playerNotFoundMessage: String
        get() = getMessage("playerNotFound")
    val playerNotAccessibleMessage: String
        get() = getMessage("playerNotAccessible")
    val pickpocketDisabledMessage: String
        get() = getMessage("pickpocketDisabled")
    val pickpocketDisabledOtherMessage: String
        get() = getMessage("pickpocketDisabledOther")
    val pickpocketToggleOnMessage: String
        get() = getMessage("pickpocketToggleOn")
    val pickpocketToggleOffMessage: String
        get() = getMessage("pickpocketToggleOff")
    val participationTogglingDisabled: String
        get() = getMessage("participationTogglingDisabled")
    val personCantBeStolenFromMessage: String
        get() = getMessage("personCantBeStolenFrom")
    val pickpocketRegionDisallowMessage: String
        get() = getMessage("pickpocketRegionDisallow")
    val pickpocketUnsuccessfulMessage: String
        get() = getMessage("pickpocketUnsuccessful")
    val pickpocketSuccessfulMessage: String
        get() = getMessage("pickpocketSuccessful")
    val participatingTrueNotificationMessage: String
        get() = getMessage("participatingTrue")
    val participatingFalseNotificationMessage: String
        get() = getMessage("participatingFalse")
    val pickpocketOnMoveWarningMessage: String
        get() = getMessage("pickpocketOnMoveWarning")
    val pickpocketOnMoveOtherWarningMessage: String
        get() = getMessage("pickpocketOnMoveWarningOther")
    val pickpocketVictimWarningMessage: String
        get() = getMessage("pickpocketVictimWarning")
    val pickpocketNoticedWarningMessage: String
        get() = getMessage("pickpocketNoticeWarning")
    val playerStealFromAfkMessage: String
        get() = getMessage("playerAfk")
    val playerStealWhileAfk: String
        get() = getMessage("attemptStealWhileAfk")
    val noMoneyReceivedMessage: String
        get() = getMessage("noMoneyReceived")
    fun getAdminStatusChangeMessage(value: Any) = getMessage("adminStatusChange", value.toString())
    fun getBypassStatusChangeMessage(value: Any) = getMessage("bypassStatusChange", value.toString())
    fun getBypassStatusChangeOtherMessage(player: Player, value: Any) = getMessage("bypassStatusChangeOther", player, value.toString())
    fun getExemptStatusChangeMessage(value: Any) = getMessage("exemptStatusChange", value.toString())
    fun getExemptStatusChangeOtherMessage(player: Player, value: Any) = getMessage("exemptStatusChangeOther", player, value.toString())
    fun getCooldownNoticeMessage(value: Any) = getMessage("cooldownNotice", value.toString())
    fun getMoneyAmountReceivedMessage(value: Any) = getMessage("moneyAmountReceived", value.toString())
    fun getPickpocketSuccessAdminNotificationMessage(player: Player, victim: Player) = getMessage("pickpocketSuccessAdminNotification", player, victim)
    fun getPickpocketFailureAdminNotification(player: Player, victim: Player) = getMessage("pickpocketFailureAdminNotification", player, victim)
    fun getProfileAssignSuccessMessage(profile: String, player: Player) = getMessage("profileAssignSuccess", profile, player.name)
    fun getProfileAssignFailureMessage(profile: String, player: Player) = getMessage("profileAssignFailure", profile, player.name)
    fun getProfileNotAssignedMessage() = getMessage("profileNotAssigned")
    fun getProfileThiefCreateSuccessMessage(value: String) = getMessage("profileThiefCreate", value)
    fun getProfileVictimCreateSuccessMessage(value: String) = getMessage("profileVictimCreate", value)
    fun getProfileErrorAlreadyExistsMessage(value: String) = getMessage("profileAlreadyExists", value)
    fun getProfileNotFoundMessage(value: String) = getMessage("profileNotFound", value)
    fun getProfileChangePropertyMessage(vararg values: String) = getMessage("profileChangeProperty", *values)
    fun getProfileRemovedMessage(value: String) = getMessage("profileRemoved", value)

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }
}