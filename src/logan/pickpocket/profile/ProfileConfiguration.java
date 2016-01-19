package logan.pickpocket.profile;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Created by Tre on 1/18/2016.
 */
public class ProfileConfiguration {
    private final String itemSection = "items";
    private final String adminSection = "admin";
    private final String bypassSection = "bypass";
    private final String exemptSection = "exempt";

    private String path;
    private File file;
    private YamlConfiguration yamlConfiguration;

    public ProfileConfiguration(String path) {
        this.path = path;
        initFile(path);
    }

    public ProfileConfiguration(String directory, String fileName) {
        this.path = directory + fileName;
        initFile(directory + fileName);
    }

    private void initFile(String path) {
        file = new File(path);
        yamlConfiguration = new YamlConfiguration();
    }

    public void createSections() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                yamlConfiguration.createSection(itemSection);
                yamlConfiguration.createSection(adminSection);
                yamlConfiguration.createSection(bypassSection);
                yamlConfiguration.createSection(exemptSection);
                yamlConfiguration.save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Created profile sections in path " + path + ".");
    }

    public String getItemSection() {
        return itemSection;
    }

    public String getAdminSection() {
        return adminSection;
    }

    public String getBypassSection() {
        return bypassSection;
    }

    public String getExemptSection() {
        return exemptSection;
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }
}
