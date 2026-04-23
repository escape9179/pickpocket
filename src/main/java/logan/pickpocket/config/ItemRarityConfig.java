package logan.pickpocket.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import logan.pickpocket.main.PickpocketPlugin;

/**
 * Accessor for values in {@code item_rarities.yml}.
 */
public final class ItemRarityConfig {

    private static File file;
    private static YamlConfiguration config;
    private static List<RarityEntry> entries = Collections.emptyList();
    private static Map<Material, RarityEntry> entriesByMaterial = Collections.emptyMap();

    private ItemRarityConfig() {
    }

    /**
     * Initializes item rarity configuration from disk.
     *
     * @param plugin active plugin instance
     */
    public static void init(PickpocketPlugin plugin) {
        file = new File(plugin.getDataFolder(), "item_rarities.yml");
        config = YamlConfiguration.loadConfiguration(file);
        entries = loadEntries(config);
    }

    /**
     * Reloads item rarities from disk.
     */
    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        entries = loadEntries(config);
    }

    /**
     * Returns all valid rarity entries sorted by material key.
     *
     * @return immutable rarity entry list
     */
    public static List<RarityEntry> getEntries() {
        return entries;
    }

    /**
     * Returns the rarity entry for a material, if configured.
     *
     * @param material item material
     * @return configured rarity entry or null
     */
    public static RarityEntry getEntry(Material material) {
        if (material == null) {
            return null;
        }
        return entriesByMaterial.get(material);
    }

    /**
     * Returns the configured success chance for a material.
     *
     * @param material item material
     * @return configured chance in [0,1], or 1.0 when no entry exists
     */
    public static double getSuccessChance(Material material) {
        RarityEntry entry = getEntry(material);
        return entry == null ? 1D : entry.getValue();
    }

    /**
     * Formats rarity success chance lore in the shared list/rummage style.
     *
     * @param chance success chance as a decimal in [0,1]
     * @return lore line for chance display
     */
    public static String formatSuccessChanceLore(double chance) {
        RarityTier tier = resolveTier(chance);
        long roundedPercentage = Math.round(chance * 100D);
        return ChatColor.GRAY + "Success chance: " + tier.getColor() + roundedPercentage + "%";
    }

    /**
     * Formats rarity lore for a material using configured fallback behavior.
     *
     * @param material item material
     * @return lore line for chance display
     */
    public static String formatSuccessChanceLore(Material material) {
        return formatSuccessChanceLore(getSuccessChance(material));
    }

    /**
     * Resolves rarity tier for a configured value.
     *
     * @param value configured rarity value
     * @return matching tier enum
     */
    public static RarityTier resolveTier(double value) {
        for (RarityTier tier : RarityTier.values()) {
            if (value >= tier.getMin() && value <= tier.getMax()) {
                return tier;
            }
        }
        return RarityTier.COMMON;
    }

    private static List<RarityEntry> loadEntries(YamlConfiguration yaml) {
        List<RarityEntry> loaded = new ArrayList<>();
        Map<Material, RarityEntry> loadedByMaterial = new HashMap<>();
        for (String key : yaml.getKeys(false)) {
            Material material = Material.matchMaterial(key.toUpperCase(Locale.ROOT));
            if (material == null || !material.isItem()) {
                continue;
            }
            RarityEntry entry = new RarityEntry(material, key, yaml.getDouble(key));
            loaded.add(entry);
            loadedByMaterial.put(material, entry);
        }

        loaded.sort(Comparator.comparing(RarityEntry::getYamlKey));
        entriesByMaterial = Collections.unmodifiableMap(loadedByMaterial);
        return Collections.unmodifiableList(loaded);
    }

    /**
     * Rarity entry loaded from {@code item_rarities.yml}.
     */
    public static final class RarityEntry {
        private final Material material;
        private final String yamlKey;
        private final double value;

        public RarityEntry(Material material, String yamlKey, double value) {
            this.material = material;
            this.yamlKey = yamlKey;
            this.value = value;
        }

        public Material getMaterial() {
            return material;
        }

        public String getYamlKey() {
            return yamlKey;
        }

        public double getValue() {
            return value;
        }
    }

    /**
     * Tier mapping from rarity values documented in {@code item_rarities.yml}.
     */
    public enum RarityTier {
        COMMON("Common", ChatColor.GREEN, 0.66, 0.80),
        UNCOMMON("Uncommon", ChatColor.DARK_GREEN, 0.51, 0.65),
        RARE("Rare", ChatColor.BLUE, 0.31, 0.50),
        EPIC("Epic", ChatColor.DARK_PURPLE, 0.16, 0.30),
        MYTHIC("Mythic", ChatColor.GOLD, 0.01, 0.15);

        private final String displayName;
        private final ChatColor color;
        private final double min;
        private final double max;

        RarityTier(String displayName, ChatColor color, double min, double max) {
            this.displayName = displayName;
            this.color = color;
            this.min = min;
            this.max = max;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ChatColor getColor() {
            return color;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }
    }
}
