package logan.pickpocket.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
        for (String key : yaml.getKeys(false)) {
            Material material = Material.matchMaterial(key.toUpperCase(Locale.ROOT));
            if (material == null || !material.isItem()) {
                continue;
            }
            loaded.add(new RarityEntry(material, key, yaml.getDouble(key)));
        }

        loaded.sort(Comparator.comparing(RarityEntry::getYamlKey));
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
