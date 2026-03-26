package logan.pickpocket.config;

import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileConfiguration {

    private final File file;
    private YamlConfiguration config;

    public ProfileConfiguration() {
        this.file = new File(PickpocketPlugin.getInstance().getDataFolder(), "profiles.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        if (!config.isConfigurationSection("profiles.default")) {
            config.createSection("profiles.default");
            config.set("profiles.default.cooldown", 10);
            config.set("profiles.default.canUseFishingRod", false);
            config.set("profiles.default.minigameRollRate", 20);
            config.set("profiles.default.maxRummageCount", 5);
            config.set("profiles.default.numberOfRummageItems", 4);
            config.set("profiles.default.rummageDuration", 3);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile() {
        return file;
    }

    public List<Profile> loadProfiles() {
        List<Profile> profiles = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("profiles");
        if (section == null) return profiles;
        for (String key : section.getKeys(false)) {
            Profile profile = loadProfile(key);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        return profiles;
    }

    public Profile loadProfile(String name) {
        ConfigurationSection profileSection = config.getConfigurationSection("profiles." + name);
        if (profileSection == null) return null;
        Profile profile = new Profile(name);
        for (String propKey : profile.getProperties().keySet()) {
            String value = profileSection.getString(propKey);
            if (value != null) {
                profile.getProperties().put(propKey, value);
            }
        }
        return profile;
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean createProfile(String name) {
        if (config.isConfigurationSection("profiles." + name)) return false;
        Profile profile = new Profile(name);
        for (var entry : profile.getProperties().entrySet()) {
            config.set("profiles." + name + "." + entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean removeProfile(String name) {
        ConfigurationSection section = config.getConfigurationSection("profiles");
        if (section != null && section.isConfigurationSection(name)) {
            config.set("profiles." + name, null);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public boolean saveProfile(Profile profile) {
        ConfigurationSection profileSection = config.getConfigurationSection("profiles." + profile.getName());
        if (profileSection == null) return false;
        for (var entry : profile.getProperties().entrySet()) {
            profileSection.set(entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
