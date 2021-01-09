package logan.pickpocket.config;

import logan.api.config.CommentedConfiguration;
import logan.api.util.ColorUtils;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Represents a configuration file used
 * for setting message preferences for the plugin.
 */
public class MessageConfiguration {

    public static final String ADMIN_STATUS_CHANGE_KEY = "admin-status-change";
    public static final String BYPASS_STATUS_CHANGE_KEY = "bypass-status-change";
    public static final String BYPASS_STATUS_CHANGE_OTHER_KEY = "bypass-status-other-change";
    public static final String EXEMPT_STATUS_CHANGE_KEY = "exempt-status-change";
    public static final String EXEMPT_STATUS_CHANGE_OTHER_KEY = "exempt-status-change-other";
    public static final String RELOAD_NOTIFICATION_KEY = "reload-notification";
    public static final String PLAYER_NOT_FOUND_KEY = "player-not-found";
    public static final String PLAYER_NOT_ACCESSIBLE_KEY = "player-not-accessible";
    public static final String PICKPOCKET_DISABLED_KEY = "pickpocket-disabled";
    public static final String PICKPOCKET_DISABLED_OTHER_KEY = "pickpocket-disabled-other";
    public static final String PICKPOCKET_TOGGLE_ON_KEY = "pickpocket-toggle-on";
    public static final String PICKPOCKET_TOGGLE_OFF_KEY = "pickpocket-toggle-off";
    public static final String PERSON_CANT_BE_STOLEN_FROM_KEY = "person-cant-be-stolen-from";
    public static final String PICKPOCKET_REGION_DISALLOW_KEY = "pickpocket-region-disallow";
    public static final String PICKPOCKET_UNSUCCESSFUL_KEY = "pickpocket-unsuccessful";
    public static final String PICKPOCKET_SUCCESSFUL_KEY = "pickpocket-successful";
    public static final String PARTICIPATING_TRUE_NOTIFICATION_KEY = "participating-true-notification";
    public static final String PARTICIPATING_FALSE_NOTIFICATION_KEY = "participating-false-notification";
    public static final String PICKPOCKET_ON_MOVE_WARNING_KEY = "pickpocket-on-move-warning";
    public static final String PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY = "pickpocket-on-move-other-warning";
    public static final String PICKPOCKET_VICTIM_WARNING_KEY = "pickpocket-victim-warning";
    public static final String PICKPOCKET_NOTICED_WARNING_KEY = "pickpocket-noticed-warning";
    public static final String COOLDOWN_NOTICE_KEY = "cooldown-notice";
    public static final String NO_MONEY_RECEIVED = "no-money-received";
    public static final String MONEY_AMOUNT_RECEIVED = "money-amount-received";
    public static final String PICKPOCKET_SUCCESS_ADMIN_NOTIFICATION = "admin-notify-success";
    public static final String PICKPOCKET_FAILURE_ADMIN_NOTIFICATION = "admin-notify-failure";
    public static final String PLAYER_STEAL_FROM_AFK = "player-attempt-steal-from-afk";
    public static final String PLAYER_STEAL_WHILE_AFK = "player-attempt-steal-while-afk";

    private static CommentedConfiguration config;

    public static void create() {
        config = new CommentedConfiguration(new File(PickpocketPlugin.getInstance().getDataFolder(), "messages.yml"));

        config.createKeyIfNoneExists(ADMIN_STATUS_CHANGE_KEY, "&7Pickpocket Admin status set to %value%.");
        config.createKeyIfNoneExists(BYPASS_STATUS_CHANGE_KEY, "&7Your bypass status has been changed to %value%.");
        config.createKeyIfNoneExists(BYPASS_STATUS_CHANGE_OTHER_KEY, "&7Changed %player%'s bypass status to %value%.");
        config.createKeyIfNoneExists(EXEMPT_STATUS_CHANGE_KEY, "&7Your exempt status has been changed to %value%.");
        config.createKeyIfNoneExists(EXEMPT_STATUS_CHANGE_OTHER_KEY, "&7Changed %player's exempt status to %value%.");
        config.createKeyIfNoneExists(RELOAD_NOTIFICATION_KEY, "&aReloaded Pickpocket configuration.");
        config.createKeyIfNoneExists(PLAYER_NOT_FOUND_KEY, "&cPlayer not found.");
        config.createKeyIfNoneExists(PLAYER_NOT_ACCESSIBLE_KEY, "&cThat player is not accessible.");
        config.createKeyIfNoneExists(PICKPOCKET_DISABLED_KEY, "&cYou have pick-pocketing disabled.");
        config.createKeyIfNoneExists(PICKPOCKET_DISABLED_OTHER_KEY, "&cThat player has pick-pocketing disabled.");
        config.createKeyIfNoneExists(PICKPOCKET_TOGGLE_ON_KEY, "&7Pick-pocketing is now enabled.");
        config.createKeyIfNoneExists(PICKPOCKET_TOGGLE_OFF_KEY, "&7Pick-pocketing is now disabled.");
        config.createKeyIfNoneExists(PERSON_CANT_BE_STOLEN_FROM_KEY, "&7This person cannot be stolen from.");
        config.createKeyIfNoneExists(PICKPOCKET_REGION_DISALLOW_KEY, "&cPick-pocketing is disabled in this region.");
        config.createKeyIfNoneExists(PICKPOCKET_UNSUCCESSFUL_KEY, "&cPickpocket attempt unsuccessful.");
        config.createKeyIfNoneExists(PICKPOCKET_SUCCESSFUL_KEY, "&aPickpocket attempt successful.");
        config.createKeyIfNoneExists(PARTICIPATING_TRUE_NOTIFICATION_KEY, "&7You are currently participating in pick-pocketing.");
        config.createKeyIfNoneExists(PARTICIPATING_FALSE_NOTIFICATION_KEY, "&7You are currently not participating in pick-pocketing.");
        config.createKeyIfNoneExists(PICKPOCKET_ON_MOVE_WARNING_KEY, "&cYou cannot move whilst pick-pocketing.");
        config.createKeyIfNoneExists(PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY, "&cThe player moved.");
        config.createKeyIfNoneExists(PICKPOCKET_VICTIM_WARNING_KEY, "&cYou feel something touch your side.");
        config.createKeyIfNoneExists(PICKPOCKET_NOTICED_WARNING_KEY, "&cYou've been noticed.");
        config.createKeyIfNoneExists(COOLDOWN_NOTICE_KEY, "&cYou must wait %value% seconds before attempting another pickpocket.");
        config.createKeyIfNoneExists(NO_MONEY_RECEIVED, "&cYou received no money.");
        config.createKeyIfNoneExists(MONEY_AMOUNT_RECEIVED, "&aYou received $%value%.");
        config.createKeyIfNoneExists(PICKPOCKET_SUCCESS_ADMIN_NOTIFICATION, "&a%player% succeeded in pick-pocketing %victim%.");
        config.createKeyIfNoneExists(PICKPOCKET_FAILURE_ADMIN_NOTIFICATION, "&c%player% failed in pick-pocketing %victim%.");
        config.createKeyIfNoneExists(PLAYER_STEAL_FROM_AFK, "&cThat player is AFK.");
        config.createKeyIfNoneExists(PLAYER_STEAL_WHILE_AFK, "&cYou cannot pick-pocket while AFK.");

        config.save();
    }

    public static void reload() {
        config.reload();
    }


    private static String getMessage(String key) {
        String keyValue = config.getConfiguration().getString(key);
        return ColorUtils.colorize(keyValue);
    }

    private static String getMessage(String key, String value) {
        String keyValue = config.getConfiguration().getString(key);
        String parsedValue = keyValue.replace("%value%", value);
        return ColorUtils.colorize(parsedValue);
    }

    private static String getMessage(String key, Player player) {
        String keyValue = config.getConfiguration().getString(key);
        String parsedValue = keyValue.replace("%player%", player.getName());
        return ColorUtils.colorize(parsedValue);
    }

    private static String getMessage(String key, Player player, String value) {
        String keyValue = config.getConfiguration().getString(key);
        String replacedValue = keyValue.replace("%player%", player.getName());
        String replacedPlayer = replacedValue.replace("%value%", value);
        return ColorUtils.colorize(replacedPlayer);
    }

    private static String getMessage(String key, Player player, Player victim) {
        String keyValue = config.getConfiguration().getString(key);
        String replacedPlayer = keyValue.replace("%player%", player.getName());
        String replacedVictim = replacedPlayer.replace("%victim%", victim.getName());
        return ColorUtils.colorize(replacedVictim);
    }


    public static String getAdminStatusChangeMessage(Object value) {
        return getMessage(ADMIN_STATUS_CHANGE_KEY, value.toString());
    }

    public static String getBypassStatusChangeMessage(Object value) {
        return getMessage(BYPASS_STATUS_CHANGE_KEY, value.toString());
    }

    public static String getBypassStatusChangeOtherMessage(Player player, Object value) {
        return getMessage(BYPASS_STATUS_CHANGE_OTHER_KEY, player, value.toString());
    }

    public static String getExemptStatusChangeMessage(Object value) {
        return getMessage(EXEMPT_STATUS_CHANGE_KEY, value.toString());
    }

    public static String getExemptStatusChangeOtherMessage(Player player, Object value) {
        return getMessage(EXEMPT_STATUS_CHANGE_OTHER_KEY, player, value.toString());
    }

    public static String getReloadNotificationMessage() {
        return getMessage(RELOAD_NOTIFICATION_KEY);
    }

    public static String getPlayerNotFoundMessage() {
        return getMessage(PLAYER_NOT_FOUND_KEY);
    }

    public static String getPlayerNotAccessibleMessage() {
        return getMessage(PLAYER_NOT_ACCESSIBLE_KEY);
    }

    public static String getPickpocketDisabledMessage() {
        return getMessage(PICKPOCKET_DISABLED_KEY);
    }

    public static String getPickpocketDisabledOtherMessage() {
        return getMessage(PICKPOCKET_DISABLED_OTHER_KEY);
    }

    public static String getPickpocketToggleOnMessage() {
        return getMessage(PICKPOCKET_TOGGLE_ON_KEY);
    }

    public static String getPickpocketToggleOffMessage() {
        return getMessage(PICKPOCKET_TOGGLE_OFF_KEY);
    }

    public static String getPersonCantBeStolenFromMessage() {
        return getMessage(PERSON_CANT_BE_STOLEN_FROM_KEY);
    }

    public static String getPickpocketRegionDisallowMessage() {
        return getMessage(PICKPOCKET_REGION_DISALLOW_KEY);
    }

    public static String getPickpocketUnsuccessfulMessage() {
        return getMessage(PICKPOCKET_UNSUCCESSFUL_KEY);
    }

    public static String getPickpocketSuccessfulMessage() {
        return getMessage(PICKPOCKET_SUCCESSFUL_KEY);
    }

    public static String getParticipatingTrueNotificationMessage() {
        return getMessage(PARTICIPATING_TRUE_NOTIFICATION_KEY);
    }

    public static String getParticipatingFalseNotificationMessage() {
        return getMessage(PARTICIPATING_FALSE_NOTIFICATION_KEY);
    }

    public static String getPickpocketOnMoveWarningMessage() {
        return getMessage(PICKPOCKET_ON_MOVE_WARNING_KEY);
    }

    public static String getPickpocketOnMoveOtherWarningMessage() {
        return getMessage(PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY);
    }

    public static String getPickpocketVictimWarningMessage() {
        return getMessage(PICKPOCKET_VICTIM_WARNING_KEY);
    }

    public static String getPickpocketNoticedWarningMessage() {
        return getMessage(PICKPOCKET_NOTICED_WARNING_KEY);
    }

    public static String getCooldownNoticeMessage(Object value) {
        return getMessage(COOLDOWN_NOTICE_KEY, value.toString());
    }

    public static String getNoMoneyReceivedMessage() {
        return getMessage(NO_MONEY_RECEIVED);
    }

    public static String getMoneyAmountReceivedMessage(Object value) {
        return getMessage(MONEY_AMOUNT_RECEIVED, value.toString());
    }

    public static String getPickpocketSuccessAdminNotificationMessage(Player player, Player victim) {
        return getMessage(PICKPOCKET_SUCCESS_ADMIN_NOTIFICATION, player, victim);
    }

    public static String getPickpocketFailureAdminNotification(Player player, Player victim) {
        return getMessage(PICKPOCKET_FAILURE_ADMIN_NOTIFICATION, player, victim);
    }

    public static String getPlayerStealFromAfkMessage() {
        return getMessage(PLAYER_STEAL_FROM_AFK);
    }

    public static String getPlayerStealWhileAfk() {
        return getMessage(PLAYER_STEAL_WHILE_AFK);
    }
}
