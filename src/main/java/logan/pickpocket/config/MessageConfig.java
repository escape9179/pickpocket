package logan.pickpocket.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.YamlConfiguration;

import logan.api.util.ColorUtils;
import logan.pickpocket.main.PickpocketPlugin;

/**
 * Configuration file used for setting message preferences for the plugin.
 */
public final class MessageConfig {

    private static File file;
    private static YamlConfiguration config;
    private static YamlConfiguration defaultConfig;

    private MessageConfig() {
        
    }

    public static void init(PickpocketPlugin plugin) {
        // Load the embedded messages.yml file
        defaultConfig = new YamlConfiguration();
        try (InputStream in = plugin.getResource("messages.yml")) {
            if (in == null) {
                throw new IllegalStateException("messages.yml missing from jar");
            }
            defaultConfig.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load embedded messages.yml", e);
        }
        
        file = new File(PickpocketPlugin.getInstance().getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    private static String getMessage(String key) {
        if (!config.contains(key)) {
            // If the key doesn't exist, add it with a default placeholder value and save the config
            String defaultValue = "&cMissing message: " + key;
            config.set(key, defaultValue);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    public static String getCooldownNoticeMessage(Object value) {
        return getMessage("cooldownNotice", value.toString());
    }

    public static String getMoneyAmountReceivedMessage(Object value) {
        return getMessage("moneyAmountReceived", value.toString());
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
