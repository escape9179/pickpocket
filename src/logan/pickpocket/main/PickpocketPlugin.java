package logan.pickpocket.main;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import logan.api.bstats.Metrics;
import logan.api.command.BasicCommand;
import logan.api.command.CommandDispatcher;
import logan.api.util.UpdateChecker;
import logan.api.wrapper.APIWrapper;
import logan.api.wrapper.APIWrapper1_13;
import logan.api.wrapper.APIWrapper1_8;
import logan.pickpocket.commands.*;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.config.PickpocketConfiguration;
import logan.pickpocket.listeners.*;
import logan.pickpocket.listeners.InventoryClickListener;
import logan.pickpocket.listeners.InventoryCloseListener;
import logan.pickpocket.listeners.PlayerInteractListener;
import logan.pickpocket.listeners.PlayerJoinListener;
import logan.pickpocket.user.PickpocketUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Tre on 12/14/2015.
 */
public class PickpocketPlugin extends JavaPlugin implements Listener {

    private static PickpocketPlugin instance;

    private Server server = getServer();
    private Logger logger = getLogger();

    private static Vector<PickpocketUser> profiles;
    private static Map<Player, Integer> cooldowns;
    private BasicCommand<CommandSender> mainCommand;
    private BasicCommand<Player> adminCommand;
    private BasicCommand<Player> bypassCommand;
    private BasicCommand<Player> exemptCommand;
    private BasicCommand<Player> toggleCommand;
    private BasicCommand<CommandSender> reloadCommand;
    private BasicCommand<Player> targetCommand;

    public static final Permission PICKPOCKET_USE = new Permission("pickpocket.use", "Allow a user to pick-pocket.");
    public static final Permission PICKPOCKET_EXEMPT = new Permission("pickpocket.exempt", "Exempt a user from being stolen from.");
    public static final Permission PICKPOCKET_BYPASS = new Permission("pickpocket.bypass", "Allows user to bypass cooldown.");
    public static final Permission PICKPOCKET_ADMIN = new Permission("pickpocket.admin", "Logs pickpocket information to admins.");
    public static final Permission PICKPOCKET_TOGGLE = new Permission("pickpocket.toggle", "Toggle pick-pocketing for yourself.");
    public static final Permission PICKPOCKET_RELOAD = new Permission("pickpocket.reload", "Reload the Pickpocket configuration file.");
    private BukkitScheduler scheduler;
    private static APIWrapper wrapper;
    private static PickpocketConfiguration pickpocketConfiguration;
    private static Economy econ = null;
    private static Essentials essentials;
    private static boolean vaultEnabled;
    private static boolean worldGuardPresent;
    private static boolean townyPresent;
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
            default:
                wrapper = new APIWrapper1_13();
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
            getLogger().info("Error resolving WorldGuard classes. Per-region pick-pocketing won't work.");
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
        MessageConfiguration.create();

        profiles = new Vector<>();
        cooldowns = new ConcurrentHashMap<>();

        mainCommand = new MainCommand();
        adminCommand = new AdminCommand();
        bypassCommand = new AdminBypassCommand();
        exemptCommand = new AdminExemptCommand();
        toggleCommand = new ToggleCommand();
        reloadCommand = new ReloadCommand();
        targetCommand = new TargetCommand();

        CommandDispatcher.Companion.registerCommand(mainCommand);
        CommandDispatcher.Companion.registerCommand(adminCommand);
        CommandDispatcher.Companion.registerCommand(bypassCommand);
        CommandDispatcher.Companion.registerCommand(exemptCommand);
        CommandDispatcher.Companion.registerCommand(reloadCommand);
        CommandDispatcher.Companion.registerCommand(targetCommand);
        CommandDispatcher.Companion.registerCommand(toggleCommand);

        new InventoryClickListener();
        new InventoryCloseListener();
        new PlayerInteractListener();
        new PlayerJoinListener();

        server.getPluginManager().registerEvents(this, this);

        scheduler = server.getScheduler();

        /* Player movement check thread timer */
        scheduler.runTaskTimer(this, () -> Bukkit.getOnlinePlayers().stream().filter(Objects::nonNull).forEach(MoveCheck::check), 5, 5);

        /* Cool-down timer */
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
            getLogger().info("Vault not found. Players won't steal money when pick-pocketing.");
        } else vaultEnabled = true;

        /* Set up Essentials */
        if ((essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials")) == null) {
            logger.info("Essentials not found. People can steal from AFK players!");
        }

        /* Set up towny */
        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            townyPresent = true;
            logger.info("Towny rules in effect. Change them in config.yml.");
        }

        /* Check for updates */
        new UpdateChecker(this, 16273).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available from https://www.spigotmc.org/resources/pickpocket.16273/.");
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
        return CommandDispatcher.Companion.onCommand(sender, command, label, args);
//        if (!(sender instanceof Player)) {
//            sender.sendMessage("You must be a player to use this command.");
//            return true;
//        }
//        Player player = (Player) sender;
//
//        if (label.equalsIgnoreCase("pickpocket") && (player.isOp() || player.hasPermission(PICKPOCKET_USE))) {
//            if (args.length == 0) {
//                sender.sendMessage(ChatColor.DARK_GRAY + NAME + " " + getDescription().getVersion());
//                sender.sendMessage(ColorUtils.colorize("/pickpocket toggle &7- Toggle pick-pocketing for yourself."));
//                sender.sendMessage(ColorUtils.colorize("/pickpocket admin &7- Toggle admin notifications for yourself."));
//                sender.sendMessage(ColorUtils.colorize("/pickpocket exempt [name] &7- Exempt yourself or another player from being stolen from."));
//                sender.sendMessage(ColorUtils.colorize("/pickpocket bypass [name] &7- Toggle cooldown bypass for yourself or another player."));
//                sender.sendMessage(ColorUtils.colorize("/pickpocket {name} &7- Pick-pocket a player near you."));
//            } else if (args[0].equalsIgnoreCase("admin") && player.hasPermission(PICKPOCKET_ADMIN)) {
//                adminCommand.run(player, profiles, args);
//            } else if (args[0].equalsIgnoreCase("exempt") && player.hasPermission(PICKPOCKET_EXEMPT)) {
//                if (args.length > 1)
//                    exemptCommand.execute(player, profiles, args[1]);
//                else exemptCommand.execute(player, profiles);
//            } else if (args[0].equalsIgnoreCase("bypass") && player.hasPermission(PICKPOCKET_BYPASS)) {
//                if (args.length > 1)
//                    bypassCommand.execute(player, profiles, args[1]);
//                else bypassCommand.execute(player, profiles);
//            } else if (args[0].equalsIgnoreCase("toggle") && player.hasPermission(PICKPOCKET_TOGGLE)) {
//                toggleCommand.execute(player, profiles);
//            } else if (args[0].equalsIgnoreCase("reload") && player.hasPermission(PICKPOCKET_RELOAD)) {
//                reloadCommand.execute(player, profiles);
//            } else if ((args.length == 1)) {
//                targetCommand.execute(player, profiles, args[0]);
//            }
//        }
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

    public static APIWrapper getAPIWrapper() {
        return wrapper;
    }

    public static PickpocketConfiguration getPickpocketConfiguration() {
        return pickpocketConfiguration;
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

    public static boolean isTownyPresent() {
        return townyPresent;
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static boolean isEssentialsPresent() {
        return essentials != null;
    }

    public static String getPluginName() {
        return instance.getDescription().getName();
    }

    public static String getPluginVersion() {
        return instance.getDescription().getVersion();
    }
}
