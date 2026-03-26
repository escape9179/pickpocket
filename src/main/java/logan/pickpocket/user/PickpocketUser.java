package logan.pickpocket.user;

import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PickpocketUser {

    private final UUID uuid;
    private PickpocketUser victim;
    private PickpocketUser predator;
    private PickpocketUser lastPredator;
    private boolean playingMinigame;
    private boolean rummaging;
    private RummageInventory openRummageInventory;
    private Minigame currentMinigame;
    private final PlayerConfiguration playerConfiguration;

    public PickpocketUser(UUID uuid) {
        this.uuid = uuid;
        this.playerConfiguration = new PlayerConfiguration(
                PickpocketPlugin.getInstance().getDataFolder() + "/players/",
                uuid + ".yml"
        );
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
        return playerConfiguration.isAdmin();
    }

    public void setAdmin(boolean value) {
        playerConfiguration.setAdmin(value);
    }

    public boolean isBypassing() {
        return playerConfiguration.isBypassing();
    }

    public void setBypassing(boolean value) {
        playerConfiguration.setBypassing(value);
    }

    public boolean isExempt() {
        return playerConfiguration.isExempt();
    }

    public void setExempt(boolean value) {
        playerConfiguration.setExempt(value);
    }

    public Minigame getCurrentMinigame() {
        return currentMinigame;
    }

    public void setCurrentMinigame(Minigame currentMinigame) {
        this.currentMinigame = currentMinigame;
    }

    public PlayerConfiguration getPlayerConfiguration() {
        return playerConfiguration;
    }

    public Profile getThiefProfile() {
        return findThiefProfile();
    }

    public void setThiefProfile(Profile profile) {
        playerConfiguration.setThiefProfile(profile != null ? profile.getName() : "default");
    }

    public int getSteals() {
        return playerConfiguration.getStealCount();
    }

    public void setSteals(int value) {
        playerConfiguration.setStealCount(value);
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
            openRummageInventory = new RummageInventory(victim);
            openRummageInventory.show(this);
            rummaging = true;
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
        String profileName = playerConfiguration.getThiefProfile();
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
        if (!playerConfiguration.isBypassing()) {
            PickpocketPlugin.addCooldown(getBukkitPlayer(), thiefProfile.getCooldown());
        }
    }

    private boolean isCoolingDown() {
        return PickpocketPlugin.getCooldowns().containsKey(getBukkitPlayer());
    }

    public void save() {
        playerConfiguration.save();
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
        for (PickpocketUser user : PickpocketPlugin.getUsers()) {
            if (user.uuid.equals(player.getUniqueId())) {
                return user;
            }
        }
        PickpocketUser user = new PickpocketUser(player.getUniqueId());
        PickpocketPlugin.addProfile(user);
        return user;
    }
}
