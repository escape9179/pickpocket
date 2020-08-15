package logan.pickpocket.profile;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tre on 1/18/2016.
 */
public class ProfileConfiguration
{
    private final String adminSection         = "admin";
    private final String bypassSection        = "bypass";
    private final String exemptSection        = "exempt";
    private final String participatingSection = "participating";

    private String            path;
    private File              file;
    private YamlConfiguration yamlConfiguration;

    public ProfileConfiguration(String directory, String fileName)
    {
        this.path = directory + fileName;
        new File(directory).mkdirs();
        file = new File(path);
        yamlConfiguration = new YamlConfiguration();
    }

    public void createSections()
    {
        try
        {
            if (!file.exists())
            {
                file.createNewFile();
                if (!yamlConfiguration.isConfigurationSection(adminSection)) yamlConfiguration.createSection(adminSection);
                if (!yamlConfiguration.isConfigurationSection(bypassSection)) yamlConfiguration.createSection(bypassSection);
                if (!yamlConfiguration.isConfigurationSection(exemptSection)) yamlConfiguration.createSection(exemptSection);
                if (!yamlConfiguration.isSet(participatingSection)) yamlConfiguration.set(participatingSection, true);
                yamlConfiguration.save(file);
            }
            else
            {
                yamlConfiguration.load(file);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void saveConfiguration()
    {
        try
        {
            yamlConfiguration.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setAdminSection(boolean bool)
    {
        yamlConfiguration.set(adminSection, bool);
        saveConfiguration();
    }

    public void setBypassSection(boolean bool)
    {
        yamlConfiguration.set(bypassSection, bool);
        saveConfiguration();
    }

    public void setExemptSection(boolean bool)
    {
        yamlConfiguration.set(exemptSection, bool);
        saveConfiguration();
    }

    public void setParticipatingSection(boolean bool)
    {
        yamlConfiguration.set(participatingSection, bool);
        saveConfiguration();
    }

    public String getAdminSection()
    {
        return adminSection;
    }

    public String getBypassSection()
    {
        return bypassSection;
    }

    public String getExemptSection()
    {
        return exemptSection;
    }

    public boolean getAdminSectionValue()
    {
        return yamlConfiguration.getBoolean(adminSection);
    }

    public boolean getBypassSectionValue()
    {
        return yamlConfiguration.getBoolean(bypassSection);
    }

    public boolean getExemptSectionValue()
    {
        return yamlConfiguration.getBoolean(exemptSection);
    }

    public boolean getParticipatingSectionValue()
    {
        return yamlConfiguration.getBoolean(participatingSection);
    }

    public YamlConfiguration getYamlConfiguration()
    {
        return yamlConfiguration;
    }
}
