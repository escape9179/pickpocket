package logan.pickpocket.main;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tre on 1/16/2016.
 */
public class PickpocketConfiguration {

    private File configFile;
    private YamlConfiguration configuration;

    PickpocketConfiguration(String path, String name) {
        configFile = new File(path + name);
        configuration = new YamlConfiguration();
    }

    void setup() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                configuration.createSection("punishments");
                configuration.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                configuration.load(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    void printKeys(Player player) {
        for (Object o : configuration.getValues(true).keySet()) {
            player.sendMessage(o.toString());
        }
        player.sendMessage(String.valueOf((configuration.getValues(true).size())));
    }
}
