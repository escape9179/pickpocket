package logan.api.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public interface BasicConfiguration {
    File getFile();
    YamlConfiguration getConfiguration();
    void setConfiguration(YamlConfiguration configuration);

    default void createKeyIfNoneExists(String key, Object value) {
        YamlConfigurationUtil.setIfNotSet(getConfiguration(), key, value);
    }

    default void createKeyIfNoneExists(String key) {
        createKeyIfNoneExists(key, null);
    }

    default void reload() {
        setConfiguration(YamlConfiguration.loadConfiguration(getFile()));
    }

    default void setValue(String key, Object value) {
        getConfiguration().set(key, value);
    }

    default void save() {
        try {
            getConfiguration().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
