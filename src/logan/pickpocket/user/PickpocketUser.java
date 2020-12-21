package logan.pickpocket.user;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;


/**
 * Created by Tre on 12/14/2015.
 */
public class PickpocketUser {

    private UUID uuid;
    private PickpocketUser victim;
    private PickpocketUser predator;
    private PickpocketUser recentPredator;
    private boolean playingMinigame;
    private boolean rummaging;
    private RummageInventory openRummageInventory;
    private boolean participating;

    private ProfileConfiguration profileConfiguration;
    private MinigameModule minigameModule;

    public PickpocketUser(UUID uuid) {
        this.uuid = uuid;

        profileConfiguration = new ProfileConfiguration(PickpocketPlugin.getInstance().getDataFolder() + "/players/", uuid.toString() + ".yml");
        profileConfiguration.createSections();

        participating = profileConfiguration.getParticipatingSectionValue();

        minigameModule = new MinigameModule(this);
    }

    public void performPickpocket(PickpocketUser victim) {

        minigameModule = new MinigameModule(this);

        boolean isAllowedPickpocketing = true;
        if (PickpocketPlugin.isWorldGuardPresent()) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(getPlayer());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            Location worldEditLocation = BukkitAdapter.adapt(getPlayer().getLocation());
            isAllowedPickpocketing = query.testState(worldEditLocation, localPlayer, PickpocketPlugin.PICKPOCKET_FLAG);
        }

        if (!isAllowedPickpocketing) {
            getPlayer().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_REGION_DISALLOW_KEY));
            return;
        }
        if (!PickpocketPlugin.getCooldowns().containsKey(uuid)) {
            openRummageInventory = new RummageInventory(victim);
            openRummageInventory.show(this);
            setRummaging(true);
            setVictim(victim);
            victim.setPredator(this);
        } else {
            getPlayer().sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.COOLDOWN_NOTICE_KEY, PickpocketPlugin.getCooldowns().get(uuid).toString()));
        }
    }

    public RummageInventory getOpenRummageInventory() {
        return openRummageInventory;
    }

    public boolean isPlayingMinigame() {
        return playingMinigame;
    }

    public boolean isRummaging() {
        return rummaging;
    }

    public void setRummaging(boolean value) {
        rummaging = value;
    }

    public void setPlayingMinigame(boolean value) {
        playingMinigame = value;
    }

    public void setVictim(PickpocketUser victim) {
        this.victim = victim;
    }

    public PickpocketUser getVictim() {
        return victim;
    }

    public void setPredator(PickpocketUser predator) {
        this.predator = predator;
        if (predator != null) {
            recentPredator = predator;
        }
    }

    public PickpocketUser getPredator() {
        return predator;
    }

    public PickpocketUser getRecentPredator() {
        return recentPredator;
    }

    public boolean isPredator() {
        return victim != null;
    }

    public boolean isVictim() {
        return predator != null;
    }

    public void setParticipating(boolean participating) {
        this.participating = participating;
        profileConfiguration.setParticipatingSection(participating);
    }

    public boolean isParticipating() {
        return participating;
    }

    public void setPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }

    public MinigameModule getMinigameModule() {
        return minigameModule;
    }

    public void sendMessage(String message, Object... args) {
        getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
    }

    public void playRummageSound() {
        getPlayer().playSound(getPlayer().getLocation(), PickpocketPlugin.getAPIWrapper().getSoundBlockSnowStep(), 1.0f, 0.5f);
    }

    @Override
    public String toString() {
        return "player: " + (uuid != null ? getPlayer().getName() : "null") + "\nvictim: " + (victim != null ? victim.getPlayer().getName() : "null")
                + "\npredator: " + (predator != null ? predator.getPlayer().getName() : "null") + "\nrecent predator: " + (recentPredator != null ? recentPredator.getPlayer().getName() : "null"
                + "\nmini-game: " + playingMinigame + "\nrummaging: " + rummaging + "\nparticipating: " + participating);
    }
}