package logan.pickpocket.config;

import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Config {

    private static File file;
    private static YamlConfiguration config;

    private Config() {
    }

    public static void init() {
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

    public static boolean isDatabaseEnabled() {
        return config.getBoolean("database.enabled");
    }

    public static String getDatabaseServer() {
        return config.getString("database.server");
    }

    public static String getDatabaseUsername() {
        return config.getString("database.username");
    }

    public static String getDatabasePassword() {
        return config.getString("database.password");
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
}
