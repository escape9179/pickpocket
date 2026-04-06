package logan.api.config;

import org.bukkit.configuration.file.YamlConfiguration;

public final class YamlConfigurationUtil {

    private YamlConfigurationUtil() {
    }

    public static boolean isNotSet(YamlConfiguration config, String key) {
        return !config.isSet(key);
    }

    public static void setIfNotSet(YamlConfiguration config, String key, Object value) {
        if (isNotSet(config, key)) {
            config.set(key, value);
        }
    }
}
