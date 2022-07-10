package logan.pickpocket.main

import com.earth2me.essentials.Essentials
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry
import logan.api.bstats.Metrics
import logan.api.command.CommandDispatcher
import logan.api.command.CommandDispatcher.Companion.registerCommand
import logan.api.gui.GUIAPI
import logan.api.util.UpdateChecker
import logan.pickpocket.commands.*
import logan.pickpocket.config.PickpocketConfiguration
import logan.pickpocket.config.ProfileConfiguration
import logan.pickpocket.listeners.*
import logan.pickpocket.user.PickpocketUser
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Tre on 12/14/2015.
 */
class PickpocketPlugin : JavaPlugin() {
    override fun onLoad() {
        // Load WorldGuard classes.
        val worldGuardClass: Class<WorldGuard>
        val flagRegistryClass: Class<FlagRegistry>
        val stateFlagClass: Class<StateFlag>
        try {
            worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard") as Class<WorldGuard>
            flagRegistryClass =
                Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry") as Class<FlagRegistry>
            stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag") as Class<StateFlag>
        } catch (e: ClassNotFoundException) {
            logger.info("Error resolving WorldGuard classes. Per-region pick-pocketing won't work.")
            isWorldGuardPresent = false
            return
        }
        isWorldGuardPresent = true

        // Register custom WorldGuard flags if WorldGuard is present.
        try {
            val flagRegistry: FlagRegistry
            val worldGuardInstance = worldGuardClass.getMethod("getInstance").invoke(null) as WorldGuard
            flagRegistry = worldGuardClass.getMethod("getFlagRegistry").invoke(worldGuardInstance) as FlagRegistry
            val pickpocketFlagName = "pickpocket"
            val stateFlag = stateFlagClass
                .getConstructor(String::class.java, Boolean::class.javaPrimitiveType)
                .newInstance(pickpocketFlagName, true)
            flagRegistryClass.getMethod("register", Flag::class.java).invoke(flagRegistry, stateFlag)
            PICKPOCKET_FLAG = stateFlag
        } catch (e: Exception) {
            e.printStackTrace()
        } // TODO Add code
        // This could be a FlagConflictException
    }

    override fun onEnable() {
        instance = this
        dataFolder.mkdirs()

        //
        // Create and initialize configuration files.
        //
        try {
            Files.copy(javaClass.getResourceAsStream("/config.yml")!!, Paths.get(dataFolder.path + "/config.yml"))
            Files.copy(javaClass.getResourceAsStream("/messages.yml")!!, Paths.get(dataFolder.path + "/messages.yml"))
        } catch (e: FileAlreadyExistsException) {
            //TODO Don't use exception handling to produce correct functionality
        }

        createConfigurations()

        users = Vector()

        registerCommand(MainCommand())
        registerCommand(AdminCommand())
        registerCommand(AdminBypassCommand())
        registerCommand(AdminExemptCommand())
        registerCommand(ReloadCommand())
        registerCommand(TargetCommand())
        registerCommand(ToggleCommand())
        registerCommand(StatusCommand())
        registerCommand(ProfileCommand())
        registerCommand(ProfileCreateCommand())
        registerCommand(ProfileEditCommand())
        registerCommand(ProfileRemoveCommand())
        registerCommand(ProfileViewCommand())
        registerCommand(ProfileAssignCommand())

        // Register API listeners
        GUIAPI.registerListeners(this)
        GUIAPI.registerInventoryClickListener(InventoryClickListener())
        GUIAPI.registerInventoryCloseListener(InventoryCloseListener())
        PlayerInteractListener()
        PlayerJoinListener()
        server.pluginManager.registerEvents(ProjectileHitListener(), this)
        val scheduler = server.scheduler

        /* Player movement check thread timer */
        scheduler.runTaskTimer(this, Runnable {
            Bukkit.getOnlinePlayers().stream().filter { obj: Any? -> Objects.nonNull(obj) }
                .forEach { player: Player -> MoveCheck.check(player) }
        }, 5, 5)

        /* Cool-down timer */
        scheduler.runTaskTimerAsynchronously(this, Runnable {
            for (player in cooldowns.keys) {
                cooldowns[player] = cooldowns[player]!! - 1
                if (cooldowns[player]!! <= 0) cooldowns.remove(player)
            }
        }, 20, 20)

        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        val pluginId = 8568 // <-- Replace with the id of your plugin!
        Metrics(this, pluginId)

        // Set-up Vault economy
        if (!setupEconomy()) {
            logger.info("Vault not found. Players won't steal money when pick-pocketing.")
        } else isVaultEnabled = true

        /* Set up Essentials */
        (server.pluginManager.getPlugin("Essentials") as? Essentials).also { essentials = it } ?: run {
            logger.info("Essentials not found. People can steal from AFK players!")
        }

        /* Set up towny */
        if (server.pluginManager.getPlugin("Towny") != null) isTownyPresent = true.also {
            logger.info("Towny rules in effect. Change them in config.yml.")
        }

        /* Check for updates */
        UpdateChecker(this, 16273).getVersion { version: String? ->
            if (description.version.equals(version, ignoreCase = true)) {
                logger.info("There is not a new update available.")
            } else {
                logger.info("There is a new update available from https://www.spigotmc.org/resources/pickpocket.16273/.")
                logger.info(String.format("Pickpocket %s -> %s", description.version, version))
            }
        }

        logger.info("$name enabled.")
    }

    override fun onDisable() {
        logger.info("$name disabled.")
    }

    private fun createConfigurations() {
        profileConfiguration = ProfileConfiguration()
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return false
        economy = rsp.provider
        return economy != null
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return CommandDispatcher.onCommand(sender, command, label, args)
    }

    companion object {
        lateinit var instance: PickpocketPlugin
            private set
        var users = Vector<PickpocketUser>()
            private set
        private val cooldowns = ConcurrentHashMap<Player, Int>()
        val PICKPOCKET_USE: Permission = Permission("pickpocket.use", "Allow a user to pick-pocket.")
        val PICKPOCKET_EXEMPT: Permission = Permission("pickpocket.exempt", "Exempt a user from being stolen from.")
        val PICKPOCKET_BYPASS: Permission = Permission("pickpocket.bypass", "Allows user to bypass cooldown.")
        val PICKPOCKET_ADMIN: Permission = Permission("pickpocket.admin", "Logs pickpocket information to admins.")
        val PICKPOCKET_TOGGLE: Permission = Permission("pickpocket.toggle", "Toggle pick-pocketing for yourself.")
        val PICKPOCKET_RELOAD: Permission =
            Permission("pickpocket.reload", "Reload the Pickpocket configuration file.")
        lateinit var profileConfiguration: ProfileConfiguration
            private set
        var economy: Economy? = null
            private set
        var essentials: Essentials? = null
            private set
        var isVaultEnabled = false
            private set
        var isWorldGuardPresent = false
            private set
        var isTownyPresent = false
            private set
        var PICKPOCKET_FLAG: StateFlag? = null
        fun addProfile(profile: PickpocketUser?) {
            users.add(profile)
        }

        fun addCooldown(player: Player, duration: Int) {
            cooldowns[player] = duration
        }

        fun getCooldowns(): Map<Player, Int> {
            return cooldowns
        }

        fun log(message: String) = instance.logger.info(message)

        fun registerListener(listener: Listener?) {
            instance.server.pluginManager.registerEvents(listener!!, instance)
        }

        val isEssentialsPresent: Boolean
            get() = essentials != null
        val pluginVersion: String
            get() = instance.description.version
    }
}