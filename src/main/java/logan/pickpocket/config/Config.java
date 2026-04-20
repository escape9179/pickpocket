package logan.pickpocket.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.inventory.PickpocketInventoryBlueprint;

/**
 * Accessor for values in the plugin {@code config.yml}.
 */
public final class Config {

    private static final String KEY_DEFAULT_PICKPOCKET_LAYOUT = "pickpocketInventory.defaultLayout";
    private static final int PICKPOCKET_LAYOUT_ROWS = PickpocketInventoryBlueprint.ROWS;
    private static final int PICKPOCKET_LAYOUT_COLS = PickpocketInventoryBlueprint.COLS;

    private static File file;
    private static YamlConfiguration config;
    private static YamlConfiguration defaultConfig;
    
    private Config() {
    }

    /**
     * Initializes configuration from disk and bundled defaults.
     *
     * @param plugin active plugin instance
     */
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
        initializeDefaultPickpocketLayout();
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
        initializeDefaultPickpocketLayout();
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

    /**
     * Returns the global default pickpocket blueprint, auto-healed when malformed.
     */
    public static ItemStack[] getDefaultPickpocketInventoryLayoutSnapshot() {
        return getDefaultPickpocketInventoryLayoutSnapshot(PickpocketInventoryBlueprint.MIN_USABLE_STEALABLE_SLOTS);
    }

    /**
     * Returns the global default pickpocket blueprint for the requested minimum green count.
     */
    public static ItemStack[] getDefaultPickpocketInventoryLayoutSnapshot(int requiredStealableSlots) {
        int minimumStealableSlots = PickpocketInventoryBlueprint.normalizeRequiredStealableSlots(requiredStealableSlots);
        ItemStack[] configured = parseDefaultLayoutRows(config.getStringList(KEY_DEFAULT_PICKPOCKET_LAYOUT));
        if (configured == null) {
            configured = parseDefaultLayoutRows(defaultConfig.getStringList(KEY_DEFAULT_PICKPOCKET_LAYOUT));
        }
        if (configured == null) {
            configured = PickpocketInventoryBlueprint.createDeterministicValidLayout(minimumStealableSlots);
            setDefaultPickpocketInventoryLayout(configured);
            save();
            return configured;
        }
        String invalid = PickpocketInventoryBlueprint.validate(configured, minimumStealableSlots);
        if (invalid == null) {
            return cloneContents(configured);
        }
        ItemStack[] fallback = PickpocketInventoryBlueprint.createDeterministicValidLayout(minimumStealableSlots);
        setDefaultPickpocketInventoryLayout(fallback);
        save();
        return fallback;
    }

    /**
     * Stores the global default pickpocket blueprint as editable row strings.
     */
    public static void setDefaultPickpocketInventoryLayout(ItemStack[] contents) {
        config.set(KEY_DEFAULT_PICKPOCKET_LAYOUT, serializeDefaultLayoutRows(contents));
    }

    private static void initializeDefaultPickpocketLayout() {
        if (parseDefaultLayoutRows(config.getStringList(KEY_DEFAULT_PICKPOCKET_LAYOUT)) != null) {
            return;
        }
        ItemStack[] defaults = parseDefaultLayoutRows(defaultConfig.getStringList(KEY_DEFAULT_PICKPOCKET_LAYOUT));
        if (defaults == null) {
            defaults = PickpocketInventoryBlueprint.createDeterministicValidLayout(
                    PickpocketInventoryBlueprint.MIN_USABLE_STEALABLE_SLOTS);
        }
        setDefaultPickpocketInventoryLayout(defaults);
        save();
    }

    private static ItemStack[] parseDefaultLayoutRows(List<String> rows) {
        if (rows == null || rows.size() != PICKPOCKET_LAYOUT_ROWS) {
            return null;
        }
        ItemStack[] parsed = new ItemStack[PickpocketInventoryBlueprint.SIZE];
        for (int row = 0; row < PICKPOCKET_LAYOUT_ROWS; row++) {
            String rowValue = rows.get(row);
            if (rowValue == null || rowValue.length() != PICKPOCKET_LAYOUT_COLS) {
                return null;
            }
            for (int col = 0; col < PICKPOCKET_LAYOUT_COLS; col++) {
                Material material = decodeLayoutChar(rowValue.charAt(col));
                if (material == null) {
                    return null;
                }
                parsed[(row * PICKPOCKET_LAYOUT_COLS) + col] = new ItemStack(material);
            }
        }
        return parsed;
    }

    private static List<String> serializeDefaultLayoutRows(ItemStack[] contents) {
        ItemStack[] normalized = new ItemStack[PickpocketInventoryBlueprint.SIZE];
        for (int i = 0; i < normalized.length; i++) {
            ItemStack stack = contents != null && i < contents.length ? contents[i] : null;
            normalized[i] = PickpocketInventoryBlueprint.normalizeSlot(stack);
        }
        List<String> rows = new ArrayList<>(PICKPOCKET_LAYOUT_ROWS);
        for (int row = 0; row < PICKPOCKET_LAYOUT_ROWS; row++) {
            StringBuilder rowValue = new StringBuilder(PICKPOCKET_LAYOUT_COLS);
            for (int col = 0; col < PICKPOCKET_LAYOUT_COLS; col++) {
                int slot = (row * PICKPOCKET_LAYOUT_COLS) + col;
                rowValue.append(encodeLayoutChar(normalized[slot]));
            }
            rows.add(rowValue.toString());
        }
        return rows;
    }

    private static char encodeLayoutChar(ItemStack stack) {
        return switch (PickpocketInventoryBlueprint.kindFromItem(stack)) {
            case STEALABLE -> 'G';
            case HINT -> 'B';
            default -> 'R';
        };
    }

    private static Material decodeLayoutChar(char value) {
        return switch (Character.toUpperCase(value)) {
            case 'R' -> Material.RED_STAINED_GLASS_PANE;
            case 'G' -> Material.GREEN_STAINED_GLASS_PANE;
            case 'B' -> Material.BLUE_STAINED_GLASS_PANE;
            default -> null;
        };
    }

    private static ItemStack[] cloneContents(ItemStack[] contents) {
        ItemStack[] clone = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            clone[i] = contents[i] == null ? null : contents[i].clone();
        }
        return clone;
    }
}
