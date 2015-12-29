package logan.pickpocket.main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Tre on 12/14/2015.
 */
public class Profile {

    private Player player;
    private File file;
    private YamlConfiguration configuration;
    private List<PickpocketItem> pickpocketItems;
    private PickpocketItemInventory pickpocketItemInventory;
    private Player victim;
    private boolean stealing;
    private int experience = 0;

    public Profile(Player player) {
        this.player = player;
        pickpocketItems = new Vector<>();
        setup();
        pickpocketItemInventory = new PickpocketItemInventory(this);
    }

    private void setup() {
        file = new File(PickPocket.PLUGIN_FOLDER_DIRECTORY + player.getUniqueId() + ".yml");
        configuration = new YamlConfiguration();

        if (!file.exists()) {
            try {
                file.createNewFile();
                configuration.createSection("times-stolen");
                configuration.createSection("pickpocket-items");
                configuration.createSection("experience");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (!configuration.isConfigurationSection("experience")) configuration.createSection("experience");

            try {
                configuration.load(file);
                loadExperience();
                loadPickpocketItems();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveExperience() {
        configuration.set("experience", experience);
    }

    private void loadExperience() {
        experience = configuration.getInt("experience");
    }

    private void savePickpocketItems() {
        List<String> pickpocketNames = new ArrayList<>();
        for (PickpocketItem pickpocketItem : pickpocketItems) {
            pickpocketNames.add(pickpocketItem.getRawName());
        }
        getConfiguration().set("pickpocket-items", pickpocketNames);
    }

    private void loadPickpocketItems() {
        List<?> pickpocketNames = configuration.getList("pickpocket-items");
        if (pickpocketNames == null || pickpocketNames.isEmpty()) return;
        for (Object o : pickpocketNames) {
            for (PickpocketItem pickpocketItem : PickpocketItem.values()) {
                if (o.equals(pickpocketItem.getRawName())) {
                    pickpocketItems.add(pickpocketItem);
                }
            }
        }
    }

    public void openPickpocketItemInventory() {
        pickpocketItemInventory.open();
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean givePickpocketItem(PickpocketItem pickpocketItem) {
        if (!pickpocketItems.contains(pickpocketItem)) {
            pickpocketItems.add(pickpocketItem);
            savePickpocketItems();
            player.sendMessage("You've been awarded the pickpocket item " + pickpocketItem.getName() + " (" + pickpocketItem.getExperienceValue() + "XP)" + ChatColor.RESET + "!");
            return false;
        }
        return true;
    }

    public void setStealing(Player victim) {
        if (victim != null) {
            stealing = true;
            this.victim = victim;
        } else {
            stealing = false;
            player.closeInventory();
        }
    }

    public boolean isStealing() {
        return stealing;
    }

    public void giveExperience(int experience) {
        this.experience += experience;
        saveExperience();
        player.sendMessage(ChatColor.GRAY + "You've been given " + ChatColor.WHITE + experience + ChatColor.GRAY + " experience!");
    }


    public Player getVictim() {
        return victim;
    }

    public Player getPlayer() {
        return player;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public List<PickpocketItem> getPickpocketItems() {
        return pickpocketItems;
    }

    public PickpocketItemInventory getPickpocketItemInventory() {
        return pickpocketItemInventory;
    }

    public int getExperience() {
        return experience;
    }
}
