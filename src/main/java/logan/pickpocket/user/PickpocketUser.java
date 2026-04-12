package logan.pickpocket.user;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import logan.api.config.YamlConfigurationUtil;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.managers.UserManager;
import logan.pickpocket.skills.MemorySkill;
import logan.pickpocket.skills.RevealSkill;
import logan.pickpocket.skills.SkillModule;
import logan.pickpocket.skills.SpeedSkill;

/**
 * Per-player pickpocket state and utility actions.
 */
public class PickpocketUser {

    private static final String KEY_STEALS = "steals";

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
        this.configuration = YamlConfiguration.loadConfiguration(file);

        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STEALS, 0);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.skillModule = new SkillModule(this);
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
