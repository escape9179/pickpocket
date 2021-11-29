package logan.pickpocket.user

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

/**
 * Created by Tre on 1/18/2016.
 */
class ProfileConfiguration(directory: String, fileName: String) {
    private val adminSection = "admin"
    private val bypassSection = "bypass"
    private val exemptSection = "exempt"
    private val participatingSection = "participating"
    private val path: String
    private val file: File
    private val yamlConfiguration: YamlConfiguration

    fun createSections() {
        try {
            if (!file.exists()) {
                file.createNewFile()
                if (!yamlConfiguration.isConfigurationSection(adminSection)) yamlConfiguration.createSection(
                    adminSection
                )
                if (!yamlConfiguration.isConfigurationSection(bypassSection)) yamlConfiguration.createSection(
                    bypassSection
                )
                if (!yamlConfiguration.isConfigurationSection(exemptSection)) yamlConfiguration.createSection(
                    exemptSection
                )
                if (!yamlConfiguration.isSet(participatingSection)) yamlConfiguration[participatingSection] = true
                yamlConfiguration.save(file)
            } else {
                yamlConfiguration.load(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveConfiguration() {
        try {
            yamlConfiguration.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setAdminSection(bool: Boolean) {
        yamlConfiguration[adminSection] = bool
        saveConfiguration()
    }

    fun setBypassSection(bool: Boolean) {
        yamlConfiguration[bypassSection] = bool
        saveConfiguration()
    }

    fun setExemptSection(bool: Boolean) {
        yamlConfiguration[exemptSection] = bool
        saveConfiguration()
    }

    fun setParticipatingSection(bool: Boolean) {
        yamlConfiguration[participatingSection] = bool
        saveConfiguration()
    }

    val adminSectionValue: Boolean
        get() = yamlConfiguration.getBoolean(adminSection)
    val bypassSectionValue: Boolean
        get() = yamlConfiguration.getBoolean(bypassSection)
    val exemptSectionValue: Boolean
        get() = yamlConfiguration.getBoolean(exemptSection)
    val participatingSectionValue: Boolean
        get() = yamlConfiguration.getBoolean(participatingSection)

    init {
        path = directory + fileName
        File(directory).mkdirs()
        file = File(path)
        yamlConfiguration = YamlConfiguration()
    }
}