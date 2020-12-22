package logan.pickpocket.main;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import logan.bstats.Metrics;
import logan.config.MessageConfiguration;
import logan.config.PickpocketConfiguration;
import logan.pickpocket.ColorUtils;
import logan.pickpocket.commands.*;
import logan.pickpocket.listeners.*;
import logan.pickpocket.user.PickpocketUser;
import logan.wrapper.APIWrapper;
import logan.wrapper.APIWrapper1_13;
import logan.wrapper.APIWrapper1_8;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Tre on 12/14/2015.
 */
public class PickpocketPlugin extends JavaPlugin implements Listener {

    private static PickpocketPlugin instance;

    public static final String NAME = "Pickpocket";
    public static final String PLUGIN_PREFIX = "[" + NAME + "]";
    private Server server = getServer();
    private Logger logger = getLogger();

    private static Vector<PickpocketUser> profiles;
    private static Map<Player, Integer> cooldowns;
    private PickpocketCommand adminCommand;
    private PickpocketCommand bypassCommand;
    private PickpocketCommand exemptCommand;
    private PickpocketCommand toggleCommand;
    private PickpocketCommand reloadCommand;
    private PickpocketCommand targetCommand;

    public static final Permission PICKPOCKET_USE = new Permission("pickpocket.use", "Allow a user to pick-pocket.");
    public static final Permission PICKPOCKET_EXEMPT = new Permission("pickpocket.exempt", "Exempt a user from being stolen from.");
    public static final Permission PICKPOCKET_BYPASS = new Permission("pickpocket.bypass", "Allows user to bypass cooldown.");
    public static final Permission PICKPOCKET_ADMIN = new Permission("pickpocket.admin", "Logs pickpocket information to admins.");
    public static final Permission PICKPOCKET_TOGGLE = new Permission("pickpocket.toggle", "Toggle pick-pocketing for yourself.");
    public static final Permission PICKPOCKET_RELOAD = new Permission("pickpocket.reload", "Reload the Pickpocket configuration file.");
    private BukkitScheduler scheduler;
    private static APIWrapper wrapper;
    private static PickpocketConfiguration pickpocketConfiguration;
    private static MessageConfiguration messageConfiguration;
    private static Economy econ = null;
    private static Essentials essentials;
    private static boolean vaultEnabled;
    private static boolean worldGuardPresent;
    private static String pickpocketFlagName = "pickpocket";
    public static StateFlag PICKPOCKET_FLAG;

    @Override
    public void onLoad() {
        // Create an instance of a wrapper compatible with
        // the Bukkit version running on the server.
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] semanticNumbers = version.split("\\.");
        String majorMinorVersion = String.join(".", semanticNumbers[0], semanticNumbers[1]);
        switch (majorMinorVersion) {
            case "1.8":
            case "1.9":
            case "1.10":
            case "1.11":
            case "1.12":
                wrapper = new APIWrapper1_8();
                break;
            case "1.13":
            case "1.14":
            case "1.15":
            case "1.16":
                wrapper = new APIWrapper1_13();
                break;
            default:
                PickpocketPlugin.log("Unsupported version. Disabling...");
                getServer().getPluginManager().disablePlugin(this);
        }

        // Load WorldGuard classes.
        Class<WorldGuard> worldGuardClass;
        Class<FlagRegistry> flagRegistryClass;
        Class<StateFlag> stateFlagClass;
        try {
            worldGuardClass = (Class<WorldGuard>) Class.forName("com.sk89q.worldguard.WorldGuard");
            flagRegistryClass = (Class<FlagRegistry>) Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
            stateFlagClass = (Class<StateFlag>) Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
        } catch (ClassNotFoundException e) {
            PickpocketPlugin.log("Error resolving WorldGuard classes. Per-region pick-pocketing won't work.");
            worldGuardPresent = false;
            return;
        }

        worldGuardPresent = true;

        // Register custom WorldGuard flags if WorldGuard is present.
        try {
            FlagRegistry flagRegistry;
            WorldGuard worldGuardInstance = (WorldGuard) worldGuardClass.getMethod("getInstance").invoke(null);
            flagRegistry = (FlagRegistry) worldGuardClass.getMethod("getFlagRegistry").invoke(worldGuardInstance);
            StateFlag stateFlag
                    = stateFlagClass
                    .getConstructor(String.class, boolean.class)
                    .newInstance(pickpocketFlagName, true);
            flagRegistryClass.getMethod("register", Flag.class).invoke(flagRegistry, stateFlag);
            PICKPOCKET_FLAG = stateFlag;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Add code
            // This could be a FlagConflictException
            e.printStackTrace();
        }
    }

    public void onEnable() {

        instance = this;

        getDataFolder().mkdirs();

        //
        // Create and initialize configuration files.
        //

        // Initialize main configuration file
        pickpocketConfiguration = new PickpocketConfiguration();
        pickpocketConfiguration.create();

        // Initialize and create message configuration file.
        messageConfiguration = new MessageConfiguration();
        messageConfiguration.create();

        profiles = new Vector<>();
        cooldowns = new ConcurrentHashMap<>();

        adminCommand = new AdminCommand();
        bypassCommand = new BypassCommand();
        exemptCommand = new ExemptCommand();
        toggleCommand = new ToggleCommand();
        reloadCommand = new ReloadCommand();
        targetCommand = new TargetCommand();

        new InventoryClickListener();
        new InventoryCloseListener();
        new PlayerInteractListener();
        new PlayerJoinListener();

        server.getPluginManager().registerEvents(this, this);
        server.getPluginManager().registerEvents(new PlayerMoveListener(), this);

        scheduler = server.getScheduler();

        scheduler.runTaskTimerAsynchronously(this, () -> {
            for (Player player : cooldowns.keySet()) {
                cooldowns.put(player, cooldowns.get(player) - 1);
                if (cooldowns.get(player) <= 0) cooldowns.remove(player);
            }
        }, 20, 20);

        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 8568; // <-- Replace with the id of your plugin!
        new Metrics(this, pluginId);

        // Set-up Vault economy
        if (!setupEconomy()) {
            log("Vault not found. Players won't steal money when pick-pocketing.");
        } else vaultEnabled = true;

        /* Set up Essentials */
        if ((essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials")) == null) {
            logger.info("Essentials not found. People can steal from AFK players!");
        }

        /* Check for updates */
        new UpdateChecker(this, 16273).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available.");
                logger.info(String.format("Pickpocket %s -> %s", this.getDescription().getVersion(), version));
            }
        });

        logger.info(getName() + " enabled.");
    }

    public void onDisable() {
        logger.info(getName() + " disabled.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("pickpocket") && (player.isOp() || player.hasPermission(PICKPOCKET_USE))) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.DARK_GRAY + NAME + " " + getDescription().getVersion());
                sender.sendMessage(ColorUtils.colorize("/pickpocket toggle &7- Toggle pick-pocketing for yourself."));
                sender.sendMessage(ColorUtils.colorize("/pickpocket admin &7- Toggle admin notifications for yourself."));
                sender.sendMessage(ColorUtils.colorize("/pickpocket exempt [name] &7- Exempt yourself or another player from being stolen from."));
                sender.sendMessage(ColorUtils.colorize("/pickpocket bypass [name] &7- Toggle cooldown bypass for yourself or another player."));
                sender.sendMessage(ColorUtils.colorize("/pickpocket {name} &7- Pick-pocket a player near you."));
            } else if (args[0].equalsIgnoreCase("admin") && player.hasPermission(PICKPOCKET_ADMIN)) {
                adminCommand.execute(player, profiles, args);
            } else if (args[0].equalsIgnoreCase("exempt") && player.hasPermission(PICKPOCKET_EXEMPT)) {
                if (args.length > 1)
                    exemptCommand.execute(player, profiles, args[1]);
                else exemptCommand.execute(player, profiles);
            } else if (args[0].equalsIgnoreCase("bypass") && player.hasPermission(PICKPOCKET_BYPASS)) {
                if (args.length > 1)
                    bypassCommand.execute(player, profiles, args[1]);
                else bypassCommand.execute(player, profiles);
            } else if (args[0].equalsIgnoreCase("toggle") && player.hasPermission(PICKPOCKET_TOGGLE)) {
                toggleCommand.execute(player, profiles);
            } else if (args[0].equalsIgnoreCase("reload") && player.hasPermission(PICKPOCKET_RELOAD)) {
                reloadCommand.execute(player, profiles);
            } else if ((args.length == 1)) {
                targetCommand.execute(player, profiles, args[0]);
            }
        }

        return true;
    }

    public static void addProfile(PickpocketUser profile) {
        profiles.add(profile);
    }

    public static Vector<PickpocketUser> getProfiles() {
        return profiles;
    }

    public static void addCooldown(Player player) {
        cooldowns.put(player, pickpocketConfiguration.getCooldownTime());
    }

    public static Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }

    public static PickpocketPlugin getInstance() {
        return instance;
    }

    public static File getPluginFolder() {
        return instance.getDataFolder();
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void log(String message) {
        System.out.println(PLUGIN_PREFIX + " " + message);
    }

    public static APIWrapper getAPIWrapper() {
        return wrapper;
    }

    public static PickpocketConfiguration getPickpocketConfiguration() {
        return pickpocketConfiguration;
    }

    public static MessageConfiguration getMessageConfiguration() {
        return messageConfiguration;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public static boolean isWorldGuardPresent() {
        return worldGuardPresent;
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static boolean isEssentialsPresent() {
        return essentials != null;
    }
}
