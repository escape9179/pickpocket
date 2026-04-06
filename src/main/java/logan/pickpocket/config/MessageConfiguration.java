package logan.pickpocket.config;

import logan.api.util.ColorUtils;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Configuration file used for setting message preferences for the plugin.
 */
public final class MessageConfiguration {

    private static File file;
    private static YamlConfiguration config;

    private MessageConfiguration() {
    }

    public static void init() {
        file = new File(PickpocketPlugin.getInstance().getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    private static String getMessage(String key) {
        String keyValue = config.getString(key);
        return ColorUtils.colorize(keyValue);
    }

    private static String getMessage(String key, String... values) {
        String keyValue = config.getString(key);
        for (String value : values) {
            keyValue = keyValue.replaceFirst("%value%", value);
        }
        return ColorUtils.colorize(keyValue);
    }

    private static String getMessageWithPlayerAndValue(String key, Player player, String value) {
        String keyValue = config.getString(key, "");
        return ColorUtils.colorize(keyValue.replace("%player%", player.getName()).replace("%value%", value));
    }

    private static String getMessageWithPlayers(String key, Player player, Player victim) {
        String keyValue = config.getString(key, "");
        return ColorUtils.colorize(keyValue.replace("%player%", player.getName()).replace("%victim%", victim.getName()));
    }

    public static String getReloadNotificationMessage() {
        return getMessage("pluginReload");
    }

    public static String getPlayerNotFoundMessage() {
        return getMessage("playerNotFound");
    }

    public static String getPickpocketToggleOnMessage() {
        return getMessage("pickpocketToggleOn");
    }

    public static String getPickpocketToggleOffMessage() {
        return getMessage("pickpocketToggleOff");
    }

    public static String getPickpocketDisabledMessage() {
        return getMessage("pickpocketDisabled");
    }

    public static String getPersonCantBeStolenFromMessage() {
        return getMessage("personCantBeStolenFrom");
    }

    public static String getPickpocketRegionDisallowMessage() {
        return getMessage("pickpocketRegionDisallow");
    }

    public static String getPickpocketUnsuccessfulMessage() {
        return getMessage("pickpocketUnsuccessful");
    }

    public static String getPickpocketSuccessfulMessage() {
        return getMessage("pickpocketSuccessful");
    }

    public static String getPickpocketOnMoveWarningMessage() {
        return getMessage("pickpocketOnMoveWarning");
    }

    public static String getPlayerStealFromAfkMessage() {
        return getMessage("playerAfk");
    }

    public static String getPlayerStealWhileAfk() {
        return getMessage("attemptStealWhileAfk");
    }

    public static String getPickpocketAttemptMessage() {
        return getMessage("pickpocketAttempt");
    }

    public static String getPickpocketCancelledMovedMessage() {
        return getMessage("pickpocketCancelledMoved");
    }

    public static String getPickpocketCancelledTargetMovedMessage() {
        return getMessage("pickpocketCancelledTargetMoved");
    }

    public static String getNoMoneyReceivedMessage() {
        return getMessage("noMoneyReceived");
    }

    public static String getAdminStatusChangeMessage(Object value) {
        return getMessage("adminStatusChange", value.toString());
    }

    public static String getBypassStatusChangeMessage(Object value) {
        return getMessage("bypassStatusChange", value.toString());
    }

    public static String getBypassStatusChangeOtherMessage(Player player, Object value) {
        return getMessageWithPlayerAndValue("bypassStatusChangeOther", player, value.toString());
    }

    public static String getExemptStatusChangeMessage(Object value) {
        return getMessage("exemptStatusChange", value.toString());
    }

    public static String getExemptStatusChangeOtherMessage(Player player, Object value) {
        return getMessageWithPlayerAndValue("exemptStatusChangeOther", player, value.toString());
    }

    public static String getCooldownNoticeMessage(Object value) {
        return getMessage("cooldownNotice", value.toString());
    }

    public static String getMoneyAmountReceivedMessage(Object value) {
        return getMessage("moneyAmountReceived", value.toString());
    }

    public static String getPickpocketSuccessAdminNotificationMessage(Player player, Player victim) {
        return getMessageWithPlayers("pickpocketSuccessAdminNotification", player, victim);
    }

    public static String getPickpocketFailureAdminNotification(Player player, Player victim) {
        return getMessageWithPlayers("pickpocketFailureAdminNotification", player, victim);
    }

    public static String getProfileAssignSuccessMessage(String profile, Player player) {
        return getMessage("profileAssignSuccess", profile, player.getName());
    }

    public static String getProfileAssignFailureMessage(String profile, Player player) {
        return getMessage("profileAssignFailure", profile, player.getName());
    }

    public static String getProfileNotAssignedMessage() {
        return getMessage("profileNotAssigned");
    }

    public static String getProfileCreateSuccessMessage(String value) {
        return getMessage("profileCreate", value);
    }

    public static String getProfileErrorAlreadyExistsMessage(String value) {
        return getMessage("profileAlreadyExists", value);
    }

    public static String getProfileNotFoundMessage(String value) {
        return getMessage("profileNotFound", value);
    }

    public static String getProfileChangePropertyMessage(String... values) {
        return getMessage("profileChangeProperty", values);
    }

    public static String getProfileRemovedMessage(String value) {
        return getMessage("profileRemoved", value);
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
