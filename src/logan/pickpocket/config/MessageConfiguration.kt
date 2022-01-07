package logan.pickpocket.config

import de.leonhard.storage.Yaml
import logan.api.util.ColorUtils
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.entity.Player

/**
 * Configuration file used for setting message preferences for the plugin.
 */
object MessageConfiguration {

    private val config = Yaml("messages.yml", PickpocketPlugin.instance.dataFolder.path)

    init {
        config["adminStatusChange"] = "&7Pickpocket Admin status set to %value%."
        config.yamlEditor.write(listOf("Testing write"))
        config["bypassStatusChange"] = "&7Your bypass status has been changed to %value%."
        config["bypassStatusChangeOther"] = "&7Changed %player%'s bypass status to %value%."
        config["exemptStatusChange"] = "&7Your exempt status has been changed to %value%."
        config["exemptStatusChangeOther"] = "&7Changed %player's exempt status to %value%."
        config["pluginReload"] = "&aReloaded Pickpocket configuration."
        config["playerNotFound"] = "&cPlayer not found."
        config["playerNotAccessible"] = "&cThat player is not accessible."
        config["pickpocketDisabled"] = "&cYou have pick-pocketing disabled."
        config["pickpocketDisabledOther"] = "&cThat player has pick-pocketing disabled."
        config["pickpocketToggleOn"] = "&7Pick-pocketing is now enabled."
        config["pickpocketToggleOff"] = "&7Pick-pocketing is now disabled."
        config["personCantBeStolenFrom"] = "&7This person cannot be stolen from."
        config["pickpocketRegionDisallow"] = "&cPick-pocketing is disabled in this region."
        config["pickpocketUnsuccessful"] = "&cPickpocket attempt unsuccessful."
        config["pickpocketSuccessful"] = "&aPickpocket attempt successful."
        config["participatingTrue"] = "&7You are currently participating in pick-pocketing."
        config["participatingFalse"] = "&7You are currently not participating in pick-pocketing."
        config["pickpocketOnMoveWarning"] = "&cYou cannot move whilst pick-pocketing."
        config["pickpocketOnMoveWarningOther"] = "&cThe player moved."
        config["pickpocketVictimWarning"] = "&cYou feel something touch your side."
        config["pickpocketNoticedWarning"] = "&cYou've been noticed."
        config["cooldownNotice"] = "&cYou must wait %value% seconds before attempting another pickpocket."
        config["noMoneyReceived"] = "&cYou received no money."
        config["moneyAmountReceived"] = "&aYou received $%value%."
        config["pickpocketSuccessAdminNotification"] = "&a%player% succeeded in pick-pocketing %victim%."
        config["pickpocketFailureAdminNotification"] = "&c%player% failed in pick-pocketing %victim%."
        config["playerAfk"] = "&cThat player is AFK."
        config["attemptStealWhileAfk"] = "&cYou cannot pick-pocket while AFK."
        config["profileNotAssigned"] = "&cYou haven't been assigned a profile."
        config["profileThiefCreate"] = "&aSuccessfully created thief profile %value%."
        config["profileVictimCreate"] = "&aSuccessfully created victim profile %value%."
        config["profileAlreadyExists"] = "&cProfile %value% already exists."
        config["profileNotFound"] = "&cCouldn't find profile %value%."
        config["profileChangeProperty"] = "&aChanged property %value% from %value% to %value% in profile %value%."
        config["profileRemoved"] = "Removed profile %value%."
    }

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
    fun getProfileNotAssignedMessage() = getMessage("profileNotAssigned")
    fun getProfileThiefCreateSuccessMessage(value: String) = getMessage("profileThiefCreate", value)
    fun getProfileVictimCreateSuccessMessage(value: String) = getMessage("profileVictimCreate", value)
    fun getProfileErrorAlreadyExistsMessage(value: String) = getMessage("profileAlreadyExists", value)
    fun getProfileNotFoundMessage(value: String) = getMessage("profileNotFound", value)
    fun getProfileChangePropertyMessage(vararg values: String) = getMessage("profileChangeProperty", *values)
    fun getProfileRemovedMessage(value: String) = getMessage("profileRemoved", value)

    fun reload() {
        TODO()
    }
}