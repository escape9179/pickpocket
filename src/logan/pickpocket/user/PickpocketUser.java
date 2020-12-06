package logan.pickpocket.user;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.wrapper.APIWrapper1_13;
import logan.wrapper.APIWrapper1_8;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * Created by Tre on 12/14/2015.
 */
public class PickpocketUser {

    private Player player;
    private PickpocketUser victim;
    private PickpocketUser predator;
    private boolean playingMinigame;
    private boolean rummaging;
    private RummageInventory openRummageInventory;
    private boolean participating;

    private ProfileConfiguration profileConfiguration;
    private MinigameModule minigameModule;

    public PickpocketUser(Player player) {
        this.player = player;

        profileConfiguration = new ProfileConfiguration(PickpocketPlugin.getInstance().getDataFolder() + "/players/", player.getUniqueId().toString() + ".yml");
        profileConfiguration.createSections();

        participating = profileConfiguration.getParticipatingSectionValue();

        minigameModule = new MinigameModule(this);
    }

    public void performPickpocket(PickpocketUser victim) {
        boolean isAllowedPickpocketing = false;
        if (PickpocketPlugin.getAPIWrapper() instanceof APIWrapper1_8) {
            // Old (WorldGuard pre-7.0) way of checking world guard region flags.
            com.sk89q.worldguard.LocalPlayer localPlayer = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player);
            com.sk89q.worldguard.bukkit.RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
            com.sk89q.worldguard.bukkit.RegionQuery query = container.createQuery();
            Location playerLocation = player.getLocation();
            isAllowedPickpocketing = query.testState(playerLocation, localPlayer, PickpocketPlugin.PICKPOCKET_FLAG);
        } else if (PickpocketPlugin.getAPIWrapper() instanceof APIWrapper1_13) {
            // New way (WorldGuard 7.0) way of checking region flags.
            com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            com.sk89q.worldguard.protection.regions.RegionContainer container
                    = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
            com.sk89q.worldguard.protection.regions.RegionQuery query = container.createQuery();
            com.sk89q.worldedit.util.Location worldEditLocation = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.getLocation());
            isAllowedPickpocketing = query.testState(worldEditLocation, localPlayer, PickpocketPlugin.PICKPOCKET_FLAG);
        }

        if (!isAllowedPickpocketing) {
            player.sendMessage("Pick-pocketing isn't allowed here.");
            return;
        }
        if (!PickpocketPlugin.getCooldowns().containsKey(player)) {
            openRummageInventory = new RummageInventory(victim);
            openRummageInventory.show(this);
            setRummaging(true);
            setVictim(victim);
            victim.setPredator(this);
        } else {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.COOLDOWN_NOTICE_KEY, PickpocketPlugin.getCooldowns().get(player).toString()));
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
    }

    public PickpocketUser getPredator() {
        return predator;
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }

    public MinigameModule getMinigameModule() {
        return minigameModule;
    }

    public void sendMessage(String message, Object... args) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
    }

    public void playRummageSound() {
        player.playSound(player.getLocation(), PickpocketPlugin.getAPIWrapper().getSoundBlockSnowStep(), 1.0f, 0.5f);
    }
}