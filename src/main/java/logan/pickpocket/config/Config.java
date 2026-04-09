package logan.pickpocket.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import logan.pickpocket.main.PickpocketPlugin;

public final class Config {

    private static File file;
    private static YamlConfiguration config;
    private static YamlConfiguration defaultConfig;
    
    private Config() {
    }

    public static void init(PickpocketPlugin plugin) {
        // Load the embedded config.yml file
        defaultConfig = new YamlConfiguration();
        try (InputStream in = plugin.getResource("config.yml")) {
            if (in == null) {
                throw new IllegalStateException("config.yml missing from jar");
            }
            defaultConfig.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load embedded config.yml", e);
        }
        file = new File(PickpocketPlugin.getInstance().getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean isPickpocketingEnabled() {
        return config.getBoolean("pickpocketingEnabled");
    }

    public static void setPickpocketingEnabled(boolean value) {
        config.set("pickpocketingEnabled", value);
    }

    public static boolean isMoneyCanBeStolen() {
        return config.getBoolean("money.canBeStolen");
    }

    public static double getMoneyPercentageToSteal() {
        return config.getDouble("money.percentageToSteal");
    }

    public static List<String> getDisabledItems() {
        return computeDisabledItems();
    }

    public static int getRequiredClicksToPickpocket() {
        return config.getInt("requiredClicksToPickpocket");
    }

    public static boolean isStatusOnInteract() {
        return config.getBoolean("statusOnInteract");
    }

    public static boolean isForeignTownTheft() {
        return config.getBoolean("foreignTownTheft");
    }

    public static boolean isSameTownTheft() {
        return config.getBoolean("sameTownTheft");
    }

    private static List<String> computeDisabledItems() {
        List<String> finalItems = new ArrayList<>();
        for (String item : config.getStringList("disabledItems")) {
            char first = item.charAt(0);
            if (first == '*') {
                for (Material mat : Material.values()) {
                    finalItems.add(mat.name().toLowerCase());
                }
            } else if (first == '-') {
                finalItems.remove(item.substring(1));
            } else {
                finalItems.add(item);
            }
        }
        return finalItems;
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static float getMinSpeedSkillDelay() {
        return (float) config.getDouble("skills.speed.minDelaySeconds", 0.0);
    }
}
