package logan.pickpocket.profile;

import logan.config.MessageConfiguration;
import logan.pickpocket.RummageInventory;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/14/2015.
 */
public class Profile {

    private Player player;
    private Player victim;
    private Player predator;
    private boolean playingMinigame;
    private boolean rummaging;
    private boolean participating;

    private ProfileConfiguration profileConfiguration;
    private MinigameModule minigameModule;

    public Profile(Player player) {
        this.player = player;

        profileConfiguration = new ProfileConfiguration(PickpocketPlugin.getInstance().getDataFolder() + "/players/", player.getUniqueId().toString() + ".yml");
        profileConfiguration.createSections();

        participating = profileConfiguration.getParticipatingSectionValue();

        minigameModule = new MinigameModule(this);
    }

    public void performPickpocket(Player victim) {
        if (!PickpocketPlugin.getCooldowns().containsKey(player)) {
            RummageInventory rummageInventory = new RummageInventory(victim);
            rummageInventory.show(player);
            setRummaging(true);
            setVictim(victim);
        } else {
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.COOLDOWN_NOTICE_KEY, PickpocketPlugin.getCooldowns().get(player).toString()));
        }
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

    public void setVictim(Player victim) {
        if (victim != null) {
            this.victim = victim;
            Profiles.get(victim).setPredator(player);
        } else {
            this.victim = null;
        }
    }

    public Player getVictim() {
        return victim;
    }

    public void setPredator(Player predator) {
        this.predator = predator;
    }

    public Player getPredator() {
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

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public ProfileConfiguration getProfileConfiguration()
    {
        return profileConfiguration;
    }

    public MinigameModule getMinigameModule()
    {
        return minigameModule;
    }
}