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

public class PickpocketUser {

    private static final String KEY_STEALS = "steals";

    private final UUID uuid;
    private File file;
    private YamlConfiguration configuration;
    private SkillModule skillModule;

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

    public SpeedSkill getSpeedSkill() {
        return skillModule.getSpeedSkill();
    }

    public RevealSkill getRevealSkill() {
        return skillModule.getRevealSkill();
    }

    public MemorySkill getMemorySkill() {
        return skillModule.getMemorySkill();
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    public SkillModule getSkillModule() {
        return skillModule;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public int getSteals() {
        return configuration.getInt(KEY_STEALS);
    }

    public void setSteals(int value) {
        configuration.set(KEY_STEALS, value);
    }

    public void doPickpocket(PickpocketUser victim) {
        PickpocketSessionManager.startPickpocket(this, victim);
    }

    public void sendMessage(String message, Object... args) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
        }
    }

    public void playRummageSound() {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1.0f, 0.5f);
        }
    }

    public void playRummageExpandSound(float volume) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_WOOL_BREAK, volume, 0.85f);
        }
    }

    public void playRummageBlockedSound() {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.75f);
        }
    }

    public static PickpocketUser get(Player player) {
        return UserManager.getUsers().computeIfAbsent(
                player.getUniqueId(), PickpocketUser::new);
    }
}
