package logan.pickpocket.user;

import logan.api.config.BasicConfiguration;
import logan.api.config.YamlConfigurationUtil;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;
import logan.pickpocket.skills.PlayerSkills;
import logan.pickpocket.skills.Skills;
import logan.pickpocket.skills.SpeedSkill;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class PickpocketUser implements BasicConfiguration {

    private static final String KEY_ADMIN = "admin";
    private static final String KEY_BYPASS = "bypass";
    private static final String KEY_EXEMPT = "exempt";
    private static final String KEY_STEALS = "steals";
    private static final String KEY_THIEF_PROFILE = "thiefProfile";

    private final UUID uuid;
    private PickpocketUser victim;
    private PickpocketUser predator;
    private PickpocketUser lastPredator;
    private boolean playingMinigame;
    private boolean rummaging;
    private RummageInventory openRummageInventory;
    private Minigame currentMinigame;
    private File file;
    private YamlConfiguration configuration;
    private PlayerSkills skills;

    public PickpocketUser(UUID uuid) {
        this.uuid = uuid;
        String directory = PickpocketPlugin.getInstance().getDataFolder() + "/players/";
        this.file = new File(directory, uuid + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);

        YamlConfigurationUtil.setIfNotSet(configuration, KEY_ADMIN, false);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_BYPASS, false);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_EXEMPT, false);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_STEALS, 0);
        YamlConfigurationUtil.setIfNotSet(configuration, KEY_THIEF_PROFILE, "default");
        save();
        
        this.skills = new PlayerSkills(this);
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    public PlayerSkills getSkills() {
        return skills;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public PickpocketUser getVictim() {
        return victim;
    }

    public void setVictim(PickpocketUser victim) {
        this.victim = victim;
    }

    public PickpocketUser getPredator() {
        return predator;
    }

    public void setPredator(PickpocketUser predator) {
        this.predator = predator;
        if (lastPredator == null) {
            lastPredator = predator;
        }
    }

    public PickpocketUser getLastPredator() {
        return lastPredator;
    }

    public boolean isPredator() {
        return victim != null;
    }

    public boolean isVictim() {
        return predator != null;
    }

    public boolean isPlayingMinigame() {
        return playingMinigame;
    }

    public void setPlayingMinigame(boolean playingMinigame) {
        this.playingMinigame = playingMinigame;
    }

    public boolean isRummaging() {
        return rummaging;
    }

    public void setRummaging(boolean rummaging) {
        this.rummaging = rummaging;
    }

    public RummageInventory getOpenRummageInventory() {
        return openRummageInventory;
    }

    public void setOpenRummageInventory(RummageInventory openRummageInventory) {
        this.openRummageInventory = openRummageInventory;
    }

    public boolean isAdmin() {
        return configuration.getBoolean(KEY_ADMIN);
    }

    public void setAdmin(boolean value) {
        configuration.set(KEY_ADMIN, value);
    }

    public boolean isBypassing() {
        return configuration.getBoolean(KEY_BYPASS);
    }

    public void setBypassing(boolean value) {
        configuration.set(KEY_BYPASS, value);
    }

    public boolean isExempt() {
        return configuration.getBoolean(KEY_EXEMPT);
    }

    public void setExempt(boolean value) {
        configuration.set(KEY_EXEMPT, value);
    }

    public Minigame getCurrentMinigame() {
        return currentMinigame;
    }

    public void setCurrentMinigame(Minigame currentMinigame) {
        this.currentMinigame = currentMinigame;
    }

    public Profile getThiefProfile() {
        return findThiefProfile();
    }

    public void setThiefProfile(Profile profile) {
        configuration.set(KEY_THIEF_PROFILE, profile != null ? profile.getName() : "default");
    }

    public int getSteals() {
        return configuration.getInt(KEY_STEALS);
    }

    public void setSteals(int value) {
        configuration.set(KEY_STEALS, value);
    }

    public void doPickpocket(PickpocketUser victim) {
        Player player = getBukkitPlayer();
        if (!WorldGuardUtil.isPickpocketingAllowed(player)) {
            player.sendMessage(MessageConfiguration.getPickpocketRegionDisallowMessage());
        } else if (isCoolingDown()) {
            player.sendMessage(
                    MessageConfiguration.getCooldownNoticeMessage(
                            String.valueOf(PickpocketPlugin.getCooldowns().get(player))
                    )
            );
        } else {
            this.victim = victim;
            victim.setPredator(this);

            int speedLevel = this.skills.getSkillLevel(Skills.SPEED);
            float delaySeconds = new SpeedSkill().getDelaySeconds(speedLevel);
            int delayTicks = (int) (delaySeconds * 20);

            Location startLocation = player.getLocation();
            Location victimStartLocation = victim.getBukkitPlayer().getLocation();

            if (delayTicks <= 0) {
                openRummageInventory = new RummageInventory(victim);
                openRummageInventory.show(this);
                rummaging = true;
                this.skills.addExperience(Skills.SPEED, 10);
                return;
            }

            sendMessage("&eAttempting to pickpocket... Stay close!");

            new BukkitRunnable() {
                private int ticksPassed = 0;

                @Override
                public void run() {
                    Player targetPlayer = victim.getBukkitPlayer();
                    if (player == null || !player.isOnline() || targetPlayer == null || !targetPlayer.isOnline()) {
                        PickpocketUser.this.victim = null;
                        cancel();
                        return;
                    }

                    if (player.getLocation().distanceSquared(startLocation) > 0.5) {
                        sendMessage("&cPickpocketing cancelled because you moved!");
                        PickpocketUser.this.victim = null;
                        cancel();
                        return;
                    }

                    if (targetPlayer.getLocation().distanceSquared(victimStartLocation) > 0.5) {
                        sendMessage("&cPickpocketing cancelled because the target moved away!");
                        PickpocketUser.this.victim = null;
                        cancel();
                        return;
                    }

                    if (ticksPassed >= delayTicks) {
                        openRummageInventory = new RummageInventory(victim);
                        openRummageInventory.show(PickpocketUser.this);
                        rummaging = true;
                        
                        // Gain experience
                        PickpocketUser.this.skills.addExperience(Skills.SPEED, 10);
                        
                        cancel();
                    }
                    ticksPassed++;
                }
            }.runTaskTimer(PickpocketPlugin.getInstance(), 0L, 1L);
        }
    }

    public Profile findThiefProfile() {
        var thiefProfiles = PickpocketPlugin.getProfileConfiguration().loadProfiles();
        Player player = getBukkitPlayer();
        for (Profile profile : thiefProfiles) {
            if (player.hasPermission("pickpocket.profile.thief." + profile.getName())) {
                return profile;
            }
            Profile currentThiefProfile = getThiefProfileFromConfig();
            if (currentThiefProfile != null && currentThiefProfile.equals(profile)) {
                return profile;
            }
        }
        return null;
    }

    private Profile getThiefProfileFromConfig() {
        String profileName = configuration.getString(KEY_THIEF_PROFILE);
        if (profileName == null) return null;
        return PickpocketPlugin.getProfileConfiguration().loadProfile(profileName);
    }

    public boolean assignThiefProfile(String name) {
        Profile profile = PickpocketPlugin.getProfileConfiguration().loadProfile(name);
        if (profile != null) {
            setThiefProfile(profile);
            return true;
        }
        return false;
    }

    public void giveCooldown() {
        Profile thiefProfile = findThiefProfile();
        if (thiefProfile == null) return;
        if (!isBypassing()) {
            PickpocketPlugin.addCooldown(getBukkitPlayer(), thiefProfile.getCooldown());
        }
    }

    private boolean isCoolingDown() {
        return PickpocketPlugin.getCooldowns().containsKey(getBukkitPlayer());
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

    public static PickpocketUser get(Player player) {
        return PickpocketPlugin.getUsers().computeIfAbsent(
                player.getUniqueId(), PickpocketUser::new
        );
    }
}
