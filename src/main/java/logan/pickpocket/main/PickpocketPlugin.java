package logan.pickpocket.main;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import logan.api.bstats.Metrics;
import logan.api.command.CommandDispatcher;
import logan.api.gui.GUIAPI;
import logan.api.util.UpdateChecker;
import logan.pickpocket.commands.DebugCommand;
import logan.pickpocket.commands.DebugGiveRandom;
import logan.pickpocket.commands.MainCommand;
import logan.pickpocket.commands.ReloadCommand;
import logan.pickpocket.commands.SetSkillCommand;
import logan.pickpocket.commands.SkillsCommand;
import logan.pickpocket.commands.StatusCommand;
import logan.pickpocket.commands.TestCommand;
import logan.pickpocket.commands.ToggleCommand;
import logan.pickpocket.config.Config;
import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.hooks.EssentialsHook;
import logan.pickpocket.hooks.TownyHook;
import logan.pickpocket.hooks.VaultHook;
import logan.pickpocket.hooks.WorldGuardHook;
import logan.pickpocket.managers.PickpocketSessionHistoryEntry;
import logan.pickpocket.listeners.InventoryClickListener;
import logan.pickpocket.listeners.InventoryCloseListener;
import logan.pickpocket.listeners.InventoryOpenListener;
import logan.pickpocket.listeners.PlayerInteractListener;
import logan.pickpocket.tasks.MoveCheckTask;

/**
 * Main Bukkit plugin entrypoint for Pickpocket.
 */
public class PickpocketPlugin extends JavaPlugin {

    private static PickpocketPlugin instance;

    public static final Permission PICKPOCKET_USE = new Permission("pickpocket.use", "Allow a user to pick-pocket.");
    public static final Permission PICKPOCKET_EXEMPT = new Permission("pickpocket.exempt",
            "Exempt a user from being stolen from.");
    public static final Permission PICKPOCKET_BYPASS = new Permission("pickpocket.bypass",
            "Allows user to bypass cooldown.");
    public static final Permission PICKPOCKET_ADMIN = new Permission("pickpocket.admin",
            "Logs pickpocket information to admins.");
    public static final Permission PICKPOCKET_TOGGLE = new Permission("pickpocket.toggle",
            "Toggle pick-pocketing for yourself.");
    public static final Permission PICKPOCKET_RELOAD = new Permission("pickpocket.reload",
            "Reload the Pickpocket configuration file.");

    /**
     * Registers optional WorldGuard integration before enable.
     */
    @Override
    public void onLoad() {
        WorldGuardHook.onLoad(this);
    }

    /**
     * Initializes configuration, hooks, listeners, commands, and repeating tasks.
     */
    @Override
    public void onEnable() {
        instance = this;
        ConfigurationSerialization.registerClass(PickpocketSessionHistoryEntry.class);
        saveDefaultConfig();
        saveResource("messages.yml", false);

        Config.init(this);
        MessageConfig.init(this);

        registerCommands();
        registerListeners();
        setupDependencies();
        startTasks();

        // bStats
        new Metrics(this, 8568);

        /* Check for updates */
        new UpdateChecker(this, 16273).getVersion(version -> {
            if (getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW
                        + "There is a new update available from https://www.spigotmc.org/resources/pickpocket.16273/.");
                getServer().getConsoleSender().sendMessage(String.format(ChatColor.YELLOW + "Pickpocket %s -> %s",
                        getDescription().getVersion(), version));
            }
        });

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getName() + " enabled.");
    }

    /**
     * Logs plugin shutdown.
     */
    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + getName() + " disabled.");
    }

    private void registerCommands() {
        CommandDispatcher.registerCommand(new MainCommand());
        CommandDispatcher.registerCommand(new DebugCommand());
        CommandDispatcher.registerCommand(new DebugGiveRandom());
        CommandDispatcher.registerCommand(new ReloadCommand());
        CommandDispatcher.registerCommand(new SetSkillCommand());
        CommandDispatcher.registerCommand(new SkillsCommand());
        CommandDispatcher.registerCommand(new ToggleCommand());
        CommandDispatcher.registerCommand(new StatusCommand());
        CommandDispatcher.registerCommand(new TestCommand());
    }

    private void registerListeners() {
        GUIAPI.registerListeners(this);
        GUIAPI.registerInventoryClickListener(new InventoryClickListener());
        GUIAPI.registerInventoryCloseListener(new InventoryCloseListener());
        getServer().getPluginManager().registerEvents(new InventoryOpenListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    private void setupDependencies() {
        VaultHook.initialize(this);
        EssentialsHook.initialize(this);
        TownyHook.initialize(this);
    }

    private void startTasks() {
        new MoveCheckTask(this).start();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return CommandDispatcher.handleCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return CommandDispatcher.handleTabComplete(sender, command, alias, args);
    }

    /**
     * @return plugin singleton instance
     */
    public static PickpocketPlugin getInstance() {
        return instance;
    }

    /**
     * Logs a message through plugin logger when initialized.
     *
     * @param message message to log
     */
    public static void log(String message) {
        if (instance != null) {
            instance.getLogger().info(message);
        }
    }

    /**
     * @return currently loaded plugin version
     */
    public static String getPluginVersion() {
        return instance.getDescription().getVersion();
    }
}
