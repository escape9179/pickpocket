package logan.pickpocket.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import logan.pickpocket.main.PickpocketPlugin;

/**
 * Accessor for values in the plugin {@code config.yml}.
 */
public final class Config {

    private static File file;
    private static YamlConfiguration config;
    
    private Config() {
    }

    /**
     * Initializes configuration from disk and bundled defaults.
     *
     * @param plugin active plugin instance
     */
    public static void init(PickpocketPlugin plugin) {
        file = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * @return whether pickpocketing is globally enabled
     */
    public static boolean isPickpocketingEnabled() {
        return config.getBoolean("pickpocketingEnabled");
    }

    /**
     * Updates whether pickpocketing is globally enabled.
     *
     * @param value enabled state
     */
    public static void setPickpocketingEnabled(boolean value) {
        config.set("pickpocketingEnabled", value);
    }

    /**
     * @return whether money theft is enabled
     */
    public static boolean isMoneyCanBeStolen() {
        return config.getBoolean("money.canBeStolen");
    }

    /**
     * @return percentage of victim balance to steal
     */
    public static double getMoneyPercentageToSteal() {
        return config.getDouble("money.percentageToSteal");
    }

    /**
     * @return disabled item names from config rule expansion
     */
    public static List<String> getDisabledItems() {
        return computeDisabledItems();
    }

    /**
     * @return number of required interaction clicks
     */
    public static int getRequiredClicksToPickpocket() {
        return config.getInt("requiredClicksToPickpocket");
    }

    /**
     * @return whether status messages are shown during interactions
     */
    public static boolean isStatusOnInteract() {
        return config.getBoolean("statusOnInteract");
    }

    /**
     * @return whether stealing from foreign town residents is allowed
     */
    public static boolean isForeignTownTheft() {
        return config.getBoolean("foreignTownTheft");
    }

    /**
     * @return whether stealing from same-town residents is allowed
     */
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

    /**
     * Reloads config values from disk.
     */
    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * @return minimum configured speed skill delay in seconds
     */
    public static float getMinSpeedSkillDelay() {
        return (float) config.getDouble("skills.speed.minDelaySeconds", 0.0);
    }

    /**
     * Persists in-memory config values to disk.
     */
    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            PickpocketPlugin.log("Failed saving config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
