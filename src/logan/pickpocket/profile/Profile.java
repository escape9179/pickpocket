package logan.pickpocket.profile;

import logan.pickpocket.main.PickpocketItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Tre on 12/14/2015.
 */
public class Profile {

    private Player player;
    private Player victim;
    private boolean stealing;

    private PickpocketItemInventory pickpocketItemInventory;
    private ProfileConfiguration profileConfiguration;
    private PickpocketItemModule pickpocketItemModule;
    private PickpocketItemLoader pickpocketItemLoader;
    private StatisticModule statisticModule;

    public Profile(Player player) {
        this.player = player;

        profileConfiguration = new ProfileConfiguration("plugins/Pickpocket/players/", player.getUniqueId().toString() + ".yml");
        profileConfiguration.createSections();

        pickpocketItemLoader = new PickpocketItemLoader();
        statisticModule = new StatisticModule();

        pickpocketItemModule = new PickpocketItemModule();
        pickpocketItemModule.setPickpocketItemIntegerMap(pickpocketItemLoader.loadPickpocketItemsFromYamlConfiguration(profileConfiguration));

        pickpocketItemInventory = new PickpocketItemInventory(this);
    }

    public void openPickpocketItemInventory() {
        pickpocketItemInventory.open();
    }

    public void givePickpocketItem(PickpocketItem pickpocketItem) {
        pickpocketItemModule.addPickpocketItem(pickpocketItem);
        pickpocketItemLoader.writePickpocketItemsToYamlConfiguration(profileConfiguration, pickpocketItemModule.getPickpocketItemIntegerMap());
        player.sendMessage("Theft of " + pickpocketItem.getName() + ChatColor.RESET + " successful.");
    }

    public void setStealing(Player victim) {
        if (victim != null) {
            stealing = true;
            this.victim = victim;
        }
        else {
            stealing = false;
            player.closeInventory();
        }
    }

    public boolean isStealing() {
        return stealing;
    }

    public Player getVictim() {
        return victim;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public PickpocketItemModule getPickpocketItemModule() {
        return pickpocketItemModule;
    }

    public StatisticModule getStatisticModule() {
        return statisticModule;
    }

    public ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }
}