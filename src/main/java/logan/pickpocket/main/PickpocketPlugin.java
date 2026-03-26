package logan.pickpocket.main;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import logan.api.bstats.Metrics;
import logan.api.command.CommandDispatcher;
import logan.api.gui.GUIAPI;
import logan.api.util.UpdateChecker;
import logan.pickpocket.commands.*;
import logan.pickpocket.config.Config;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.config.ProfileConfiguration;
import logan.pickpocket.listeners.*;
import logan.pickpocket.user.PickpocketUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tre on 12/14/2015.
 */
public class PickpocketPlugin extends JavaPlugin {

    private static PickpocketPlugin instance;
    private static Vector<PickpocketUser> users = new Vector<>();
    private static final ConcurrentHashMap<Player, Integer> cooldowns = new ConcurrentHashMap<>();
    public static final Permission PICKPOCKET_USE = new Permission("pickpocket.use", "Allow a user to pick-pocket.");
    public static final Permission PICKPOCKET_EXEMPT = new Permission("pickpocket.exempt", "Exempt a user from being stolen from.");
    public static final Permission PICKPOCKET_BYPASS = new Permission("pickpocket.bypass", "Allows user to bypass cooldown.");
    public static final Permission PICKPOCKET_ADMIN = new Permission("pickpocket.admin", "Logs pickpocket information to admins.");
    public static final Permission PICKPOCKET_TOGGLE = new Permission("pickpocket.toggle", "Toggle pick-pocketing for yourself.");
    public static final Permission PICKPOCKET_RELOAD = new Permission("pickpocket.reload", "Reload the Pickpocket configuration file.");

    private static ProfileConfiguration profileConfiguration;
    private static Economy economy;
    private static Essentials essentials;
    private static boolean vaultEnabled;
    private static boolean worldGuardPresent;
    private static boolean townyPresent;
    private static StateFlag pickpocketFlag;

    @Override
    public void onLoad() {
        // Load WorldGuard classes.
        try {
            @SuppressWarnings("unchecked")
            Class<WorldGuard> worldGuardClass = (Class<WorldGuard>) Class.forName("com.sk89q.worldguard.WorldGuard");
            @SuppressWarnings("unchecked")
            Class<FlagRegistry> flagRegistryClass = (Class<FlagRegistry>) Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
            @SuppressWarnings("unchecked")
            Class<StateFlag> stateFlagClass = (Class<StateFlag>) Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");

            worldGuardPresent = true;

            // Register custom WorldGuard flags if WorldGuard is present.
            try {
                WorldGuard worldGuardInstance = (WorldGuard) worldGuardClass.getMethod("getInstance").invoke(null);
                FlagRegistry flagRegistry = (FlagRegistry) worldGuardClass.getMethod("getFlagRegistry").invoke(worldGuardInstance);
                String pickpocketFlagName = "pickpocket";
                StateFlag stateFlag = stateFlagClass
                        .getConstructor(String.class, boolean.class)
                        .newInstance(pickpocketFlagName, true);
                flagRegistryClass.getMethod("register", Flag.class).invoke(flagRegistry, stateFlag);
                pickpocketFlag = stateFlag;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            getLogger().info("Error resolving WorldGuard classes. Per-region pick-pocketing won't work.");
            worldGuardPresent = false;
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdirs();

        //
        // Create and initialize configuration files.
        //
        try {
            Files.copy(getClass().getResourceAsStream("/config.yml"), Paths.get(getDataFolder().getPath() + "/config.yml"));
            Files.copy(getClass().getResourceAsStream("/messages.yml"), Paths.get(getDataFolder().getPath() + "/messages.yml"));
        } catch (FileAlreadyExistsException e) {
            // TODO Don't use exception handling to produce correct functionality
        } catch (Exception e) {
            e.printStackTrace();
        }

        Config.init();
        MessageConfiguration.init();
        createConfigurations();

        users = new Vector<>();

        CommandDispatcher.registerCommand(new MainCommand());
        CommandDispatcher.registerCommand(new DeveloperCommand());
        CommandDispatcher.registerCommand(new DeveloperGiveRandom());
        CommandDispatcher.registerCommand(new AdminCommand());
        CommandDispatcher.registerCommand(new AdminBypassCommand());
        CommandDispatcher.registerCommand(new AdminExemptCommand());
        CommandDispatcher.registerCommand(new ReloadCommand());
        CommandDispatcher.registerCommand(new ToggleCommand());
        CommandDispatcher.registerCommand(new StatusCommand());
        CommandDispatcher.registerCommand(new ProfileCommand());
        CommandDispatcher.registerCommand(new ProfileCreateCommand());
        CommandDispatcher.registerCommand(new ProfileEditCommand());
        CommandDispatcher.registerCommand(new ProfileRemoveCommand());
        CommandDispatcher.registerCommand(new ProfileViewCommand());
        CommandDispatcher.registerCommand(new ProfileAssignCommand());

        // Register API listeners
        GUIAPI.registerListeners(this);
        GUIAPI.registerInventoryClickListener(new InventoryClickListener());
        GUIAPI.registerInventoryCloseListener(new InventoryCloseListener());
        new PlayerInteractListener();
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(), this);

        /* Player movement check thread timer */
        getServer().getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(Objects::nonNull)
                    .forEach(MoveCheck::check);
        }, 5, 5);

        /* Cool-down timer */
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : cooldowns.keySet()) {
                cooldowns.put(player, cooldowns.get(player) - 1);
                if (cooldowns.get(player) <= 0) cooldowns.remove(player);
            }
        }, 20, 20);

        // bStats
        int pluginId = 8568;
        new Metrics(this, pluginId);

        // Set-up Vault economy
        if (!setupEconomy()) {
            getLogger().info("Vault not found. Players won't steal money when pick-pocketing.");
        } else {
            vaultEnabled = true;
        }

        /* Set up Essentials */
        var essentialsPlugin = getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin instanceof Essentials ess) {
            essentials = ess;
        } else {
            getLogger().info("Essentials not found. People can steal from AFK players!");
        }

        /* Set up towny */
        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            townyPresent = true;
            getLogger().info("Towny rules in effect. Change them in config.yml.");
        }

        /* Check for updates */
        new UpdateChecker(this, 16273).getVersion(version -> {
            if (getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "There is a new update available from https://www.spigotmc.org/resources/pickpocket.16273/.");
                getServer().getConsoleSender().sendMessage(String.format(ChatColor.YELLOW + "Pickpocket %s -> %s", getDescription().getVersion(), version));
            }
        });

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + getName() + " disabled.");
    }

    private void createConfigurations() {
        profileConfiguration = new ProfileConfiguration();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return CommandDispatcher.handleCommand(sender, command, label, args);
    }

    // Static accessors

    public static PickpocketPlugin getInstance() {
        return instance;
    }

    public static Vector<PickpocketUser> getUsers() {
        return users;
    }

    public static void addProfile(PickpocketUser profile) {
        users.add(profile);
    }

    public static void addCooldown(Player player, int duration) {
        cooldowns.put(player, duration);
    }

    public static Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }

    public static void log(String message) {
        instance.getLogger().info(message);
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static ProfileConfiguration getProfileConfiguration() {
        return profileConfiguration;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static Essentials getEssentials() {
        return essentials;
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

    public static boolean isEssentialsPresent() {
        return essentials != null;
    }

    public static String getPluginVersion() {
        return instance.getDescription().getVersion();
    }

    public static StateFlag getPickpocketFlag() {
        return pickpocketFlag;
    }
}
