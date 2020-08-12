package logan.pickpocket.profile;

import logan.config.PickpocketConfiguration;
import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.fill.UniFill;
import logan.pickpocket.main.PickpocketPlugin;
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
public class Profile
{

    private Player  player;
    private Player  victim;
    private boolean stealing;
    private boolean isPlayingMinigame;
    private boolean isRummaging;
    private boolean participating;

    private ProfileConfiguration profileConfiguration;
    private MinigameModule       minigameModule;

    public Profile(Player player)
    {
        this.player = player;

        profileConfiguration = new ProfileConfiguration("plugins/Pickpocket/players/", player.getUniqueId().toString() + ".yml");
        profileConfiguration.createSections();

        participating = profileConfiguration.getParticipatingSectionValue();

        minigameModule = new MinigameModule(this);
    }

    public void pickpocket(Player victim) {
        if (!PickpocketPlugin.getCooldowns().containsKey(player)) {
            final int numberOfRandomItems = 4;
            Menu rummageMenu = new Menu("Rummage", 4);
            rummageMenu.fill(new UniFill(Material.WHITE_STAINED_GLASS_PANE));
            MenuItem rummageButton = new MenuItem("Keep rummaging...", new ItemStack(Material.CHEST));
            rummageButton.addListener(clickEvent -> {
                populateRummageMenu(rummageMenu, this, victim, numberOfRandomItems);
                rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);
                rummageMenu.update();
                // 1/5 chance that the player will get caught while rummaging.
                if (Math.random() < PickpocketConfiguration.getCaughtChance()) {
                    victim.sendMessage(ChatColor.RED + "You feel something touch your side.");

                    // Close the rummage inventory
                    rummageMenu.close();
                    player.sendMessage(ChatColor.RED + "They notice.");
                }

                // Play a sound when rummaging
                player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1.0f, 0.5f);
            });
            populateRummageMenu(rummageMenu, this, victim, numberOfRandomItems);
            rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);
            setRummaging(true);
            rummageMenu.show(player);
        } else {
            player.sendMessage(ChatColor.RED + "You must wait " + PickpocketPlugin.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
        }
    }

    private void populateRummageMenu(Menu rummageMenu, Profile profile, Player victim, int numberOfRandomItems) {
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
                profile.setStealing(victim);
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
        return isPlayingMinigame;
    }

    public boolean isRummaging() {
        return isRummaging;
    }

    public void setRummaging(boolean value)
    {
        isRummaging = value;
    }

    public void setIsPlayingMinigame(boolean value)
    {
        isPlayingMinigame = value;
    }

    public void setStealing(Player victim)
    {
        if (victim != null)
        {
            stealing    = true;
            this.victim = victim;
            minigameModule.getMinigameMenu().setTitle("Pickpocketing " + victim.getName());
        }
        else
        {
            stealing = false;
            player.closeInventory();
        }
    }

    public void setParticipating(boolean participating)
    {
        this.participating = participating;
        profileConfiguration.setParticipatingSection(participating);
    }

    public boolean isParticipating()
    {
        return participating;
    }

    public boolean isStealing()
    {
        return stealing;
    }

    public Player getVictim()
    {
        return victim;
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