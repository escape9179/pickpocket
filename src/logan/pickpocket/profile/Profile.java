package logan.pickpocket.profile;

import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/14/2015.
 */
public class Profile
{

    private Player  player;
    private Player  victim;
    private boolean stealing;
    private boolean isPlayingMinigame;
    private boolean isRummaging;

    private ProfileConfiguration profileConfiguration;
    private MinigameModule       minigameModule;

    public Profile(Player player)
    {
        this.player = player;

        profileConfiguration = new ProfileConfiguration("plugins/Pickpocket/players/", player.getUniqueId().toString() + ".yml");
        profileConfiguration.createSections();

        minigameModule = new MinigameModule(this);
    }

    public boolean isPlayingMinigame()
    {
        return isPlayingMinigame;
    }

    public boolean isRummaging()
    {
        return isRummaging;
    }

    public void setRummaging(boolean value)
    {
        isRummaging = value;
    }

    public void setIsPlayingMinigame(boolean value)
    {
        isPlayingMinigame = value;
    }

    public void setStealing(Player victim)
    {
        if (victim != null)
        {
            stealing    = true;
            this.victim = victim;
            minigameModule.getMinigameMenu().setTitle("Pickpocketing " + victim.getName());
        }
        else
        {
            stealing = false;
            player.closeInventory();
        }
    }

    public boolean isStealing()
    {
        return stealing;
    }

    public Player getVictim()
    {
        return victim;
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