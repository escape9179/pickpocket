package logan.pickpocket.main;

import logan.bstats.Metrics;
import logan.config.PickpocketConfiguration;
import logan.pickpocket.ColorUtils;
import logan.pickpocket.commands.*;
import logan.pickpocket.listeners.*;
import logan.pickpocket.profile.Profile;
import logan.wrapper.APIWrapper;
import logan.wrapper.APIWrapper1_14;
import logan.wrapper.APIWrapper1_8;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

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

    private static Vector<Profile> profiles;
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

    public void onEnable() {

        instance = this;

        // Create an instance of a wrapper compatible with
        // the Bukkit version running on the server.
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] semanticNumbers = version.split(".");
        PickpocketPlugin.log("Semantic number size = " + semanticNumbers.length + ".");
        String majorMinorVersion = String.join(".", semanticNumbers[0], semanticNumbers[1]);
        switch (majorMinorVersion) {
            case "1.8":
            case "1.9":
            case "1.10":
            case "1.11":
            case "1.12":
            case "1.13":
                wrapper = new APIWrapper1_8();
                break;
            case "1.14":
            case "1.15":
            case "1.16":
                wrapper = new APIWrapper1_14();
                break;
            default:
                PickpocketPlugin.log("Unsupported version. Disabling...");
                getServer().getPluginManager().disablePlugin(this);
        }

        getDataFolder().mkdirs();
        PickpocketConfiguration.init();

        profiles = new Vector<>();
        cooldowns = new ConcurrentHashMap<>();

        adminCommand = new AdminCommand();
        bypassCommand = new BypassCommand();
        exemptCommand = new ExemptCommand();
        toggleCommand = new ToggleCommand();
        reloadCommand = new ReloadCommand();
        targetCommand = new TargetCommand();

        new InventoryClick();
        new InventoryClose();
        new PlayerInteract();
        new PlayerJoin();

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

        if (label.equalsIgnoreCase("pickpocket") && player.hasPermission(PICKPOCKET_USE)) {
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
    public static void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public static Vector<Profile> getProfiles() {
        return profiles;
    }

    public static void addCooldown(Player player) {
        cooldowns.put(player, PickpocketConfiguration.getCooldownTime());
    }

    public static Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }

    public static PickpocketPlugin getInstance() {
        return instance;
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
}
