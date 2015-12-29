package logan.pickpocket.main;

import logan.pickpocket.command.*;
import logan.pickpocket.events.InventoryClick;
import logan.pickpocket.events.InventoryClose;
import logan.pickpocket.events.PlayerInteract;
import logan.pickpocket.events.PlayerJoin;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Tre on 12/14/2015.
 */
public class PickPocket extends JavaPlugin {

    public static final String NAME = "Pickpocket";
    public static final String VERSION = "0.9.6";
    public static final String PLUGIN_FOLDER_DIRECTORY = "plugins/" + NAME + "/";

    private Server server = getServer();
    private Logger logger = getLogger();

    private List<Profile> profiles;
    private Map<Player, Integer> cooldowns;
    private int cooldownDelay = 8;

    private PickPocketCommand profilesCommand;
    private PickPocketCommand itemsCommand;
    private PickPocketCommand experienceCommand;
    private PickPocketCommand giveXpCommand;

    public Permission giveXpPermission = new Permission("pickpocket.givexp", "Give player pickpocket experience.");
    public Permission pickpocketBypass = new Permission("pickpocket.bypass", "Bypass pickpocketing.");
    public Permission pickpocketAdmin = new Permission("pickpocket.admin", "Logs pickpocket information to admins.");

    private BukkitScheduler scheduler;


    public void onEnable() {
        File folder = new File(PLUGIN_FOLDER_DIRECTORY);
        folder.mkdirs();

        profiles = new Vector<>();
        cooldowns = new ConcurrentHashMap<>();

        profilesCommand = new ProfilesCommand();
        itemsCommand = new ItemsCommand();
        experienceCommand = new ExperienceCommand();
        giveXpCommand = new GiveXPCommand();

        new InventoryClick(this);
        new InventoryClose(this);
        new PlayerInteract(this);
        new PlayerJoin(this);

        scheduler = server.getScheduler();
        scheduler.runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {
                ProfileHelper.saveLoadedProfiles(profiles);
            }
        }, 20 * (30), 20 * (30));

        scheduler.runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {
                for (Player player : cooldowns.keySet()) {
                    cooldowns.put(player, cooldowns.get(player) - 1);
                    if (cooldowns.get(player) <= 0) cooldowns.remove(player);
                }
            }
        }, 20, 20);

        logger.info(NAME + " " + VERSION + " enabled.");
    }

    public void onDisable() {
        logger.info(NAME + " " + VERSION + " disabled.");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("pickpocket")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.DARK_GRAY + NAME + " " + VERSION);
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket profiles' to see a list of loaded profiles.");
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket items' to see a list of your pickpocket items.");
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket exp' to check your experience.");
                sender.sendMessage(ChatColor.GRAY + "Type '/pickpocket givexp' to give yourself experience.");
            } else if (args[0].equalsIgnoreCase("profiles")) {
                profilesCommand.execute(player, command, label, profiles);
            } else if (args[0].equalsIgnoreCase("items")) {
                itemsCommand.execute(player, command, label, profiles);
            } else if (args[0].equalsIgnoreCase("xp")) {
                experienceCommand.execute(player, command, label, profiles);
            } else if (args[0].equalsIgnoreCase("givexp") && player.hasPermission(giveXpPermission)) {
                giveXpCommand.execute(player, command, label, profiles, args[1]);
            }
        }

        return true;
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void addCooldown(Player player) {
        cooldowns.put(player, cooldownDelay);
    }

    public Map<Player, Integer> getCooldowns() {
        return cooldowns;
    }
}
