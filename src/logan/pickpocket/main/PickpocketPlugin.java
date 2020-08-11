package logan.pickpocket.main;

import logan.config.PickpocketConfiguration;
import logan.pickpocket.commands.*;
import logan.pickpocket.listeners.InventoryClick;
import logan.pickpocket.listeners.InventoryClose;
import logan.pickpocket.listeners.PlayerInteract;
import logan.pickpocket.listeners.PlayerJoin;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
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
    public static final String PLUGIN_FOLDER_DIRECTORY = "plugins/" + NAME + "/";
    public static final String PLAYER_DIRECTORY = PLUGIN_FOLDER_DIRECTORY + "players/";

    private Server server = getServer();
    private Logger logger = getLogger();

    private static Vector<Profile> profiles;
    private static Map<Player, Integer> cooldowns;
    private PickpocketCommand adminCommand;
    private PickpocketCommand bypassCommand;
    private PickpocketCommand exemptCommand;
    private PickpocketCommand toggleCommand;
    private PickpocketCommand reloadCommand;

    public static final Permission PICKPOCKET_EXEMPT = new Permission("pickpocket.exempt", "Exempt a user from being stolen from.");
    public static final Permission PICKPOCKET_BYPASS = new Permission("pickpocket.bypass", "Allows user to bypass cooldown.");
    public static final Permission PICKPOCKET_ADMIN = new Permission("pickpocket.admin", "Logs pickpocket information to admins.");
    public static final Permission PICKPOCKET_TOGGLE = new Permission("pickpocket.toggle", "Toggle pick-pocketing for yourself.");
    public static final Permission PICKPOCKET_RELOAD = new Permission("pickpocket.reload", "Reload the Pickpocket configuration file.");

    private BukkitScheduler scheduler;
    private static File dataFolder;


    public void onEnable() {
        instance = this;

        File folder = new File(PLUGIN_FOLDER_DIRECTORY);
        File playerFolder = new File(PLAYER_DIRECTORY);
        folder.mkdirs();
        playerFolder.mkdirs();

        dataFolder = getDataFolder();

        profiles = new Vector<>();
        cooldowns = new ConcurrentHashMap<>();

        adminCommand = new AdminCommand();
        bypassCommand = new BypassCommand();
        exemptCommand = new ExemptCommand();
        toggleCommand = new ToggleCommand();
        reloadCommand = new ReloadCommand();

        new InventoryClick();
        new InventoryClose();
        new PlayerInteract();
        new PlayerJoin();

        server.getPluginManager().registerEvents(this, this);

        scheduler = server.getScheduler();

        scheduler.runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {
                for (Player player : cooldowns.keySet()) {
                    cooldowns.put(player, cooldowns.get(player) - 1);
                    if (cooldowns.get(player) <= 0) cooldowns.remove(player);
                }
            }
        }, 20, 20);

        logger.info(getName() + " enabled.");
    }

    public void onDisable() {
        logger.info(getName() + " disabled.");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("pickpocket")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.DARK_GRAY + NAME + " " + getDescription().getVersion());
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket toggle' to toggle pick-pocketing for yourself.");
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket admin' to receive admin notifications.");
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket exempt' [name]' to exempt yourself from being stolen from.");
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket bypass' [name]' to toggle cooldown bypass.");
                sender.sendMessage(ChatColor.DARK_GRAY + "Developer Area");
                sender.sendMessage(ChatColor.GRAY + "/pickpocket printkeys");
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
            }
        }

        return true;
    }

    public static void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public static Vector<Profile> getProfiles() {
        return profiles;
    }

    public static void addCooldown(Player player) {
        cooldowns.put(player, PickpocketConfiguration.getCooldownTime());
    }

    public Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }

    public static PickpocketPlugin getInstance() {
        return instance;
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }
}
