package logan.pickpocket.user;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import logan.api.config.YamlConfigurationUtil;
import logan.pickpocket.history.PickpocketHistoryLog;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.managers.UserManager;
import logan.pickpocket.skills.MemorySkill;
import logan.pickpocket.skills.PlayerSkill;
import logan.pickpocket.skills.RevealSkill;
import logan.pickpocket.skills.Skill;
import logan.pickpocket.skills.SkillModule;
import logan.pickpocket.skills.SpeedSkill;

/**
 * Per-player pickpocket state and utility actions.
 */
public class PickpocketUser {

    private static final String KEY_STEALS = "steals";
    private static final String KEY_STATS_PREDATOR_SUCCESSES = "stats.predator.successes";
    private static final String KEY_STATS_VICTIM_COUNT = "stats.victim.count";
    private static final String KEY_STATS_TOTAL_ITEMS_STOLEN = "stats.itemsStolen.total";
    private static final String KEY_STATS_TOTAL_RUMMAGE_MILLIS = "stats.rummage.totalMillis";
    private static final String KEY_SKILLS = "skills";

    private final UUID uuid;
    private File file;
    private YamlConfiguration configuration;
    private SkillModule skillModule;

    /**
     * Creates and initializes user data storage for a player UUID.
     *
     * @param uuid player UUID
     */
    public PickpocketUser(UUID uuid) {
        this.uuid = uuid;
        String directory = PickpocketPlugin.getInstance().getDataFolder() + "/players/";
        this.file = new File(directory, uuid + ".yml");
        File parent = this.file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        this.configuration = YamlConfiguration.loadConfiguration(file);
        this.skillModule = new SkillModule(this);

        initializeDefaults();
        loadSkillStats();
        save();
    }

    private void initializeDefaults() {
        stripLegacyCounterpartStats();
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STEALS, 0);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STATS_PREDATOR_SUCCESSES, 0);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STATS_VICTIM_COUNT, 0);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STATS_TOTAL_ITEMS_STOLEN, 0);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STATS_TOTAL_RUMMAGE_MILLIS, 0L);
        for (Skill skill : Skill.values()) {
            String skillKey = skillPath(skill);
            YamlConfigurationUtil.setIfNotSet(configuration, skillKey + ".level", 0);
            YamlConfigurationUtil.setIfNotSet(configuration, skillKey + ".exp", 0);
        }
    }

    private void stripLegacyCounterpartStats() {
        configuration.set("stats.counterparts", null);
    }

    /**
     * Persists this user's current configuration to disk.
     */
    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            PickpocketPlugin.log("Failed saving player data for " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads persisted skill values into runtime skill objects.
     */
    public void loadSkillStats() {
        loadSkill(getSpeedSkill(), Skill.SPEED);
        loadSkill(getRevealSkill(), Skill.REVEAL);
        loadSkill(getMemorySkill(), Skill.MEMORY);
    }

    /**
     * Writes current runtime skill values into config.
     */
    public void persistSkillStats() {
        persistSkill(getSpeedSkill(), Skill.SPEED);
        persistSkill(getRevealSkill(), Skill.REVEAL);
        persistSkill(getMemorySkill(), Skill.MEMORY);
    }

    private void loadSkill(PlayerSkill playerSkill, Skill skill) {
        String skillKey = skillPath(skill);
        playerSkill.setLevel(configuration.getInt(skillKey + ".level", 0));
        playerSkill.setExp(configuration.getInt(skillKey + ".exp", 0));
    }

    private void persistSkill(PlayerSkill playerSkill, Skill skill) {
        String skillKey = skillPath(skill);
        configuration.set(skillKey + ".level", playerSkill.getLevel());
        configuration.set(skillKey + ".exp", playerSkill.getExp());
    }

    private String skillPath(Skill skill) {
        return KEY_SKILLS + "." + skill.name().toLowerCase();
    }

    /**
     * @return the player's speed skill
     */
    public SpeedSkill getSpeedSkill() {
        return skillModule.getSpeedSkill();
    }

    /**
     * @return the player's reveal skill
     */
    public RevealSkill getRevealSkill() {
        return skillModule.getRevealSkill();
    }

    /**
     * @return the player's memory skill
     */
    public MemorySkill getMemorySkill() {
        return skillModule.getMemorySkill();
    }

    /**
     * @return user backing file
     */
    public File getFile() {
        return file;
    }

    /**
     * @return loaded user configuration
     */
    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Replaces the in-memory configuration reference.
     *
     * @param configuration new configuration instance
     */
    public void setConfiguration(YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return skill module for this user
     */
    public SkillModule getSkillModule() {
        return skillModule;
    }

    /**
     * @return unique id of this user
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return online Bukkit player instance, or null if offline
     */
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * @return total successful steals recorded for this user
     */
    public int getSteals() {
        return configuration.getInt(KEY_STEALS);
    }

    /**
     * Sets the recorded successful steal count.
     *
     * @param value steal count
     */
    public void setSteals(int value) {
        configuration.set(KEY_STEALS, value);
    }

    /**
     * @return successful steals performed as predator
     */
    public int getPredatorSuccesses() {
        return configuration.getInt(KEY_STATS_PREDATOR_SUCCESSES);
    }

    /**
     * Increments successful steals performed as predator.
     */
    public void incrementPredatorSuccesses() {
        configuration.set(KEY_STATS_PREDATOR_SUCCESSES, getPredatorSuccesses() + 1);
    }

    /**
     * @return times this user has been victim of a successful steal
     */
    public int getVictimCount() {
        return configuration.getInt(KEY_STATS_VICTIM_COUNT);
    }

    /**
     * Increments times this user has been victim of a successful steal.
     */
    public void incrementVictimCount() {
        configuration.set(KEY_STATS_VICTIM_COUNT, getVictimCount() + 1);
    }

    /**
     * @return total number of items successfully stolen
     */
    public int getTotalItemsStolen() {
        return configuration.getInt(KEY_STATS_TOTAL_ITEMS_STOLEN);
    }

    /**
     * Adds to total items stolen.
     *
     * @param amount amount to add
     */
    public void addTotalItemsStolen(int amount) {
        if (amount <= 0) {
            return;
        }
        configuration.set(KEY_STATS_TOTAL_ITEMS_STOLEN, getTotalItemsStolen() + amount);
    }

    /**
     * @return accumulated rummage time in milliseconds
     */
    public long getTotalRummageMillis() {
        return configuration.getLong(KEY_STATS_TOTAL_RUMMAGE_MILLIS);
    }

    /**
     * Adds elapsed rummage time.
     *
     * @param millis elapsed milliseconds
     */
    public void addRummageMillis(long millis) {
        if (millis <= 0) {
            return;
        }
        configuration.set(KEY_STATS_TOTAL_RUMMAGE_MILLIS, getTotalRummageMillis() + millis);
    }

    /**
     * UUID of the victim this player pickpocketed in the most ended sessions (see {@code history/} logs).
     * Session frequency differs from per-item steal counts.
     *
     * @return UUID if any session exists as thief, otherwise empty
     */
    public Optional<UUID> getMostStealsFromUuid() {
        return PickpocketHistoryLog.mostStealsFrom(uuid);
    }

    /**
     * UUID of the thief who pickpocketed this player in the most ended sessions.
     *
     * @return UUID if any session exists as victim, otherwise empty
     */
    public Optional<UUID> getMostStealsByUuid() {
        return PickpocketHistoryLog.mostStealsBy(uuid);
    }

    /**
     * @return resolved name (or UUID string fallback) for {@link #getMostStealsFromUuid()}
     */
    public String getMostStealsFromName() {
        return resolveName(getMostStealsFromUuid());
    }

    /**
     * @return resolved name (or UUID string fallback) for {@link #getMostStealsByUuid()}
     */
    public String getMostStealsByName() {
        return resolveName(getMostStealsByUuid());
    }

    private String resolveName(Optional<UUID> uuidValue) {
        if (uuidValue.isEmpty()) {
            return "None";
        }
        UUID id = uuidValue.get();
        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
        String name = player != null ? player.getName() : null;
        return name != null ? name : id.toString();
    }

    /**
     * Starts a pickpocket attempt against a victim user.
     *
     * @param victim target user
     */
    public void doPickpocket(PickpocketUser victim) {
        PickpocketSessionManager.startPickpocket(this, victim);
    }

    /**
     * Sends a formatted and colorized message to this player when online.
     *
     * @param message message format
     * @param args string format arguments
     */
    public void sendMessage(String message, Object... args) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
        }
    }

    /**
     * Plays the default rummage progress sound.
     */
    public void playRummageSound() {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1.0f, 0.5f);
        }
    }

    /**
     * Plays the rummage expansion sound.
     *
     * @param volume playback volume
     */
    public void playRummageExpandSound(float volume) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_WOOL_BREAK, volume, 0.85f);
        }
    }

    /**
     * Plays feedback sound when rummage expansion is blocked.
     */
    public void playRummageBlockedSound() {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.75f);
        }
    }

    /**
     * Resolves or creates a cached user object for a player.
     *
     * @param player Bukkit player
     * @return cached pickpocket user
     */
    public static PickpocketUser get(Player player) {
        return UserManager.getUsers().computeIfAbsent(
                player.getUniqueId(), PickpocketUser::new);
    }
}
