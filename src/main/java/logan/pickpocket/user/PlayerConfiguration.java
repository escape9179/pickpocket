package logan.pickpocket.user;

import logan.api.config.YamlConfigurationUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tre on 1/18/2016.
 */
public class PlayerConfiguration {

    private File file;
    private YamlConfiguration configuration;

    public PlayerConfiguration(String directory, String fileName) {
        this.file = new File(directory, fileName);
        this.configuration = YamlConfiguration.loadConfiguration(file);

        // We set these incase the users configuration doesn't contain
        // one of these keys (due to an error, or upgrading of the plugin where
        // the configuration file already exists causing the absence of new keys).
        YamlConfigurationUtil.setIfNotSet(configuration, "admin", false);
        YamlConfigurationUtil.setIfNotSet(configuration, "bypass", false);
        YamlConfigurationUtil.setIfNotSet(configuration, "exempt", false);
        YamlConfigurationUtil.setIfNotSet(configuration, "steals", 0);
        YamlConfigurationUtil.setIfNotSet(configuration, "thiefProfile", "default");
        try {
            configuration.save(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isAdmin() {
        return configuration.getBoolean("admin");
    }

    public void setAdmin(boolean value) {
        configuration.set("admin", value);
    }

    public boolean isBypassing() {
        return configuration.getBoolean("bypass");
    }

    public void setBypassing(boolean value) {
        configuration.set("bypass", value);
    }

    public boolean isExempt() {
        return configuration.getBoolean("exempt");
    }

    public void setExempt(boolean value) {
        configuration.set("exempt", value);
    }

    public int getStealCount() {
        return configuration.getInt("steals");
    }

    public void setStealCount(int value) {
        configuration.set("steals", value);
    }

    public String getThiefProfile() {
        return configuration.getString("thiefProfile");
    }

    public void setThiefProfile(String value) {
        configuration.set("thiefProfile", value);
    }
}
