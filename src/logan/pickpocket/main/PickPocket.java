package logan.pickpocket.main;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Tre on 12/14/2015.
 */
public class PickPocket extends JavaPlugin implements Listener {

    public static final String NAME = "PickPocket";
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

    private Permission giveXpPermission = new Permission("pickpocket.givexp", "Give player pickpocket experience.");

    private BukkitScheduler scheduler;

    public void onEnable() {
        File folder = new File(PLUGIN_FOLDER_DIRECTORY);
        folder.mkdirs();

        profiles = new ArrayList<>();
        cooldowns = new ConcurrentHashMap<>();

        profilesCommand = new ProfilesCommand();
        itemsCommand = new ItemsCommand();
        experienceCommand = new ExperienceCommand();
        giveXpCommand = new GiveXPCommand();

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

        server.getPluginManager().registerEvents(this, this);

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
            } else if (args[0].equalsIgnoreCase("exp")) {
                experienceCommand.execute(player, command, label, profiles);
            } else if (args[0].equalsIgnoreCase("givexp") && player.hasPermission(giveXpPermission)) {
                giveXpCommand.execute(player, command, label, profiles, args[1]);
            }
        }

        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Profile profile : profiles) {
            if (profile.getPlayer().equals(player)) {
                return;
            }
        }

        profiles.add(new Profile(player));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player player = event.getPlayer();
        if (cooldowns.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "You must wait " + cooldowns.get(player) + " more seconds before attempting another pickpocket.");
            return;
        } else cooldowns.put(player, cooldownDelay);
        Player entity = (Player) event.getRightClicked();
        player.openInventory(entity.getInventory());
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        profile.setStealing(entity);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        ItemStack currentItem = null;
        try {
            currentItem = event.getCurrentItem();
        } catch (NullPointerException e) {
            event.setCancelled(true);
        }
        if (inventory.getName().contains(PickpocketItemInventory.NAME)) {
            if (currentItem.getItemMeta().getDisplayName().equals(PickpocketItemInventory.getNextButtonName())) {
                PickpocketItemInventory.nextPage();
            }
            if (currentItem.getItemMeta().getDisplayName().equals(PickpocketItemInventory.getBackButtonName())) {
                PickpocketItemInventory.previousPage();
            }
            event.setCancelled(true);
        } else {
            Player player = (Player) event.getWhoClicked();
            Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
            if (!profile.isStealing()) return;

            for (PickpocketItem pickpocketItem : PickpocketItem.values()) {
                if (currentItem.getType().equals(pickpocketItem.getMaterial())) {
                    event.setCancelled(!testChance(profile, pickpocketItem));
                    return;
                }
            }

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "This item hasn't been added to the plugin yet!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = ProfileHelper.getLoadedProfile(player, profiles);
        if (profile.isStealing()) profile.setStealing(null);
    }

    public boolean testChance(Profile profile, PickpocketItem pickpocketItem) {
        if (Math.random() < pickpocketItem.calculateExperienceBasedChance(profile.getExperience())) {
            profile.giveExperience(pickpocketItem.getExperienceValue());
            if (profile.givePickpocketItem(pickpocketItem) == false) {
                server.broadcastMessage(ChatColor.GRAY + profile.getPlayer().getName() + ChatColor.WHITE + " recieved the pickpocket item " + pickpocketItem.getName() + " (" + pickpocketItem.getExperienceValue() + "XP)!");
            }
        } else {
            profile.getPlayer().sendMessage(ChatColor.RED + "Theft unsuccessful.");
            profile.getVictim().sendMessage(ChatColor.GRAY + profile.getPlayer().getName() + ChatColor.RED + " has attempted to steal from you.");
            profile.setStealing(null);
            return false;
        }

        return true;
    }
}
