package logan.pickpocket.main;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tre on 12/14/2015.
 */
public class Profile {

    private Player player;
    private File file;
    private YamlConfiguration configuration;
    private Map<PickpocketItem, Integer> pickpocketItems;
    private PickpocketItemInventory pickpocketItemInventory;
    private Player victim;
    private boolean stealing;
    private boolean stealExempt = false;
    private boolean admin = false;
    private boolean cooldownBypass = false;

    public Profile(Player player) {
        this.player = player;
        pickpocketItems = new ConcurrentHashMap<>();
        setup();
        pickpocketItemInventory = new PickpocketItemInventory(this);
    }

    private void setup() {
        file = new File(Pickpocket.PLUGIN_FOLDER_DIRECTORY + player.getUniqueId() + ".yml");
        configuration = new YamlConfiguration();

        try {
            if (!file.exists()) {
                file.createNewFile();
                configuration.createSection("steal-exempt");
                configuration.createSection("admin");
                configuration.createSection("cooldown-bypass");
                configuration.createSection("pickpocket-items");
                configuration.set("steal-exempt", false);
                configuration.set("admin", false);
                configuration.set("cooldown-bypass", false);
                configuration.save(file);
            } else {

                if (!configuration.isConfigurationSection("steal-exempt")) {
                    configuration.createSection("steal-exempt");
                    configuration.set("steal-exempt", false);
                }
                if (!configuration.isConfigurationSection("admin")) {
                    configuration.createSection("admin");
                    configuration.set("admin", false);
                }
                if (!configuration.isConfigurationSection("cooldown-bypass")) {
                    configuration.createSection("cooldown-bypass");
                    configuration.set("cooldown-bypass", false);
                }

                stealExempt = configuration.getBoolean("steal-exempt");
                admin = configuration.getBoolean("admin");
                cooldownBypass = configuration.getBoolean("cooldown-bypass");

                loadPickpocketItems();

                configuration.save(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void savePickpocketItems() {
        List<String> listSave = new ArrayList<>();
        for (PickpocketItem pickpocketItem : pickpocketItems.keySet()) {
            listSave.add(pickpocketItem.getRawName() + "," + pickpocketItems.get(pickpocketItem));
        }

        getConfiguration().set("pickpocket-items", listSave);
    }

    private void loadPickpocketItems() {
        List<?> pickpocketSaves = configuration.getList("pickpocket-items");
        if (pickpocketSaves == null || pickpocketSaves.isEmpty()) return;

        String[][] data = new String[pickpocketSaves.size()][2];
        for (int i = 0; i < pickpocketSaves.size(); i++) {
            data[i][0] = pickpocketSaves.get(i).toString().split(",")[0];
            data[i][1] = pickpocketSaves.get(i).toString().split(",")[1];
        }

        for (int i = 0; i < data.length; i++) {
            pickpocketItems.put(PickpocketItem.getPickpocketItemByName(data[i][0]), Integer.valueOf(data[i][1]));
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

    public void givePickpocketItem(PickpocketItem pickpocketItem) {
        if (hasPickpocketItem(pickpocketItem)) {
            pickpocketItems.put(pickpocketItem, pickpocketItems.get(pickpocketItem) + 1);
            return;
        }
        pickpocketItems.put(pickpocketItem, 1);
        savePickpocketItems();
        player.sendMessage("You've been awarded the pickpocket item " + pickpocketItem.getName() + "!");

    }

    public boolean hasPickpocketItem(PickpocketItem pickpocketItem) {
        if (pickpocketItems.containsKey(pickpocketItem)) return true;
        else return false;
    }

    public void setStealExempt(boolean bool) {
        stealExempt = bool;
        configuration.set("steal-exempt", bool);
    }

    public void setAdmin(boolean bool) {
        admin = bool;
        configuration.set("admin", bool);
    }

    public void setCooldownBypass(boolean bool) {
        cooldownBypass = bool;
        configuration.set("cooldown-bypass", bool);
    }

    public boolean isStealExempt() {
        return stealExempt;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean canCooldownBypass() {
        return cooldownBypass;
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

    public Player getVictim() {
        return victim;
    }

    public Player getPlayer() {
        return player;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public Map<PickpocketItem, Integer> getPickpocketItems() {
        return pickpocketItems;
    }

    public PickpocketItemInventory getPickpocketItemInventory() {
        return pickpocketItemInventory;
    }

    public int getTimesStolenOf(PickpocketItem pickpocketItem) {
        for (PickpocketItem item : pickpocketItems.keySet()) {
            if (item.equals(pickpocketItem)) {
                return pickpocketItems.get(item);
            }
        }
        return 0;
    }

    public int getTimesStolen() {
        int ts = 0;
        for (PickpocketItem pickpocketItem : pickpocketItems.keySet()) {
            ts += pickpocketItems.get(pickpocketItem);
        }
        return ts;
    }
}
