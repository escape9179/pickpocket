package logan.config;

import logan.pickpocket.ColorUtils;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Represents a configuration file used
 * for setting message preferences for the plugin.
 */
public class MessageConfiguration {

    private CommentedConfiguration commentedConfiguration;

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
    public static final String PICKPOCKET_UNSUCCESSFUL_KEY = "pickpocket-unsuccessful";
    public static final String PICKPOCKET_SUCCESSFUL_KEY = "pickpocket-successful";
    public static final String PARTICIPATING_TRUE_NOTIFICATION_KEY = "participating-true-notification";
    public static final String PARTICIPATING_FALSE_NOTIFICATION_KEY = "participating-false-notification";
    public static final String PICKPOCKET_ON_MOVE_WARNING_KEY = "pickpocket-on-move-warning";
    public static final String PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY = "pickpocket-on-move-other-warning";
    public static final String PICKPOCKET_VICTIM_WARNING_KEY = "pickpocket-victim-warning";
    public static final String PICKPOCKET_NOTICED_WARNING_KEY = "pickpocket-noticed-warning";
    public static final String COOLDOWN_NOTICE_KEY = "cooldown-notice";

    public MessageConfiguration() {
        commentedConfiguration = new CommentedConfiguration(new File(PickpocketPlugin.getInstance().getDataFolder(), "messages.yml"));
    }

    public void create() {
        commentedConfiguration.createKeyIfNoneExists(ADMIN_STATUS_CHANGE_KEY, "&7Pickpocket Admin status set to !value.");
        commentedConfiguration.createKeyIfNoneExists(BYPASS_STATUS_CHANGE_KEY, "&7Your bypass status has been changed to !value.");
        commentedConfiguration.createKeyIfNoneExists(BYPASS_STATUS_CHANGE_OTHER_KEY, "&7Changed !player's bypass status to !value.");
        commentedConfiguration.createKeyIfNoneExists(EXEMPT_STATUS_CHANGE_KEY, "&7Your exempt status has been changed to !value.");
        commentedConfiguration.createKeyIfNoneExists(EXEMPT_STATUS_CHANGE_OTHER_KEY, "&7Your exempt status has been changed to !value.");
        commentedConfiguration.createKeyIfNoneExists(RELOAD_NOTIFICATION_KEY, "&aReloaded Pickpocket configuration.");
        commentedConfiguration.createKeyIfNoneExists(PLAYER_NOT_FOUND_KEY, "&cPlayer not found.");
        commentedConfiguration.createKeyIfNoneExists(PLAYER_NOT_ACCESSIBLE_KEY, "&cThat player is not accessible.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_DISABLED_KEY, "&cYou have pick-pocketing disabled.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_DISABLED_OTHER_KEY, "&cThat player has pick-pocketing disabled.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_TOGGLE_ON_KEY, "&7Pick-pocketing is now enabled.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_TOGGLE_OFF_KEY, "&7Pick-pocketing is now disabled.");
        commentedConfiguration.createKeyIfNoneExists(PERSON_CANT_BE_STOLEN_FROM_KEY, "&7This person cannot be stolen from.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_UNSUCCESSFUL_KEY, "&cPickpocket attempt unsuccessful.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_SUCCESSFUL_KEY, "&aPickpocket attempt successful.");
        commentedConfiguration.createKeyIfNoneExists(PARTICIPATING_TRUE_NOTIFICATION_KEY, "&7You are currently participating in pick-pocketing.");
        commentedConfiguration.createKeyIfNoneExists(PARTICIPATING_FALSE_NOTIFICATION_KEY, "&7You are currently not participating in pick-pocketing.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_ON_MOVE_WARNING_KEY, "&cYou cannot move whilst pick-pocketing.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_ON_MOVE_OTHER_WARNING_KEY, "&cThe player moved.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_VICTIM_WARNING_KEY, "&cYou feel something touch your side.");
        commentedConfiguration.createKeyIfNoneExists(PICKPOCKET_NOTICED_WARNING_KEY, "&cYou've been noticed.");
        commentedConfiguration.createKeyIfNoneExists(COOLDOWN_NOTICE_KEY, "&cYou must wait !value seconds before attempting another pickpocket.");

        commentedConfiguration.save();
    }

    public String getMessage(String key) {
        String keyValue = commentedConfiguration.getConfiguration().getString(key);
        return ColorUtils.colorize(keyValue);
    }

    public String getMessage(String key, String value) {
        String keyValue = commentedConfiguration.getConfiguration().getString(key);
        String parsedValue = keyValue.replace("!value", value);
        return ColorUtils.colorize(parsedValue);
    }

    public String getMessage(String key, Player player) {
        String keyValue = commentedConfiguration.getConfiguration().getString(key);
        String parsedValue = keyValue.replace("!player", player.getName());
        return ColorUtils.colorize(parsedValue);
    }

    public String getMessage(String key, Player player, String value) {
        String keyValue = commentedConfiguration.getConfiguration().getString(key);
        String replacedValue = keyValue.replace("!player", player.getName());
        String replacedPlayer = replacedValue.replace("!value", value);
        return ColorUtils.colorize(replacedPlayer);
    }
}
