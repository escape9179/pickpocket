package logan.pickpocket.profile;

import logan.config.PickpocketConfiguration;
import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.fill.UniFill;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tre on 12/14/2015.
 */
public class Profile {

    private Player player;
    private Player victim;
    private Player predator;
    private boolean playingMinigame;
    private boolean rummaging;
    private boolean participating;

    private ProfileConfiguration profileConfiguration;
    private MinigameModule minigameModule;

    public Profile(Player player) {
        this.player = player;

        profileConfiguration = new ProfileConfiguration("plugins/Pickpocket/players/", player.getUniqueId().toString() + ".yml");
        profileConfiguration.createSections();

        participating = profileConfiguration.getParticipatingSectionValue();

        minigameModule = new MinigameModule(this);
    }

    public void performPickpocket(Player victim) {
        if (!PickpocketPlugin.getCooldowns().containsKey(player)) {
            final int numberOfRandomItems = 4;
            Menu rummageMenu = new Menu("Rummage", 4);
            rummageMenu.fill(new UniFill(Material.WHITE_STAINED_GLASS_PANE));
            MenuItem rummageButton = new MenuItem("Keep rummaging...", new ItemStack(Material.CHEST));
            rummageButton.addListener(clickEvent -> {
                populateRummageMenu(rummageMenu, victim, numberOfRandomItems);
                rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);
                rummageMenu.update();

                // Perform probability of getting caught
                if (Math.random() < PickpocketConfiguration.getCaughtChance()) {
                    victim.sendMessage(ChatColor.RED + "You feel something touch your side.");

                    // Close the rummage inventory
                    rummageMenu.close();
                    player.sendMessage(ChatColor.RED + "You've been noticed.");
                }

                // Play a sound when rummaging
                player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1.0f, 0.5f);
            });
            populateRummageMenu(rummageMenu, victim, numberOfRandomItems);
            rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);
            setRummaging(true);
            rummageMenu.show(player);
            setVictim(victim);
        } else {
            player.sendMessage(ChatColor.RED + "You must wait " + PickpocketPlugin.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
        }
    }

    private void populateRummageMenu(Menu rummageMenu, Player victim, int numberOfRandomItems) {
        rummageMenu.clear();
        List<ItemStack> randomItems = getRandomItemsFromPlayer(victim, numberOfRandomItems);
        rummageMenu.fill(new UniFill(Material.WHITE_STAINED_GLASS_PANE));
        for (ItemStack randomItem : randomItems) {
            int randomSlot = (int) (Math.random() * (rummageMenu.getSlots() - 9));
            MenuItem menuItem = new MenuItem(randomItem);
            menuItem.addListener(menuItemClickEvent -> {
                final ItemStack fillerItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                final int bottomRightSlot = rummageMenu.getBottomRight();
                rummageMenu.addItem(bottomRightSlot, new MenuItem(fillerItem));
                rummageMenu.update();
                rummageMenu.close();
                minigameModule.startMinigame(rummageMenu.getInventory(), menuItemClickEvent.getInventoryClickEvent().getCurrentItem());
            });
            rummageMenu.addItem(randomSlot, menuItem);
        }
    }

    private List<ItemStack> getRandomItemsFromPlayer(Player player, int numberOfItems) {
        final List<ItemStack> randomItemList = new ArrayList<>();
        final ItemStack[] storageContents = player.getInventory().getStorageContents();
        final int inventorySize = player.getInventory().getStorageContents().length;
        ItemStack randomItem;
        int randomSlot;

        outer:
        for (int i = 0; i < numberOfItems; i++) {
            randomSlot = 9 + (int) (Math.random() * (inventorySize - 9));
            randomItem = storageContents[randomSlot];

            if (randomItem == null) continue;

            // Check if the item is banned
            for (String disabledItem : PickpocketConfiguration.getDisabledItems()) {
                Material disabledItemType = Material.getMaterial(disabledItem.toUpperCase());

                // This item is disabled. Skip this random item iteration.
                if (randomItem.getType().equals(disabledItemType)) continue outer;
            }

            randomItemList.add(randomItem);
        }

        return randomItemList;
    }

    public boolean isPlayingMinigame() {
        return playingMinigame;
    }

    public boolean isRummaging() {
        return rummaging;
    }

    public void setRummaging(boolean value) {
        rummaging = value;
    }

    public void setPlayingMinigame(boolean value) {
        playingMinigame = value;
    }

    public void setVictim(Player victim) {
        if (victim != null) {
            this.victim = victim;
            Profiles.get(victim).setPredator(player);
            minigameModule.getMinigameMenu().setTitle("Pickpocketing " + victim.getName());
        } else {
            Profiles.get(this.victim).setPredator(null);
            this.victim = null;
        }
    }

    public Player getVictim() {
        return victim;
    }

    public void setPredator(Player predator) {
        this.predator = predator;
    }

    public Player getPredator() {
        return predator;
    }

    public boolean isPredator() {
        return victim != null;
    }

    public boolean isVictim() {
        return predator != null;
    }

    public void setParticipating(boolean participating) {
        this.participating = participating;
        profileConfiguration.setParticipatingSection(participating);
    }

    public boolean isParticipating() {
        return participating;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public ProfileConfiguration getProfileConfiguration()
    {
        return profileConfiguration;
    }

    public MinigameModule getMinigameModule()
    {
        return minigameModule;
    }
}