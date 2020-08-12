package logan.pickpocket.listeners;

import logan.config.PickpocketConfiguration;
import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.guiapi.fill.UniFill;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteract implements Listener {

    public PlayerInteract() {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        PickpocketPlugin pickpocketPlugin = PickpocketPlugin.getInstance();

        if (!(event.getRightClicked() instanceof Player) ||
                !event.getHand().equals(EquipmentSlot.OFF_HAND) ||
                !event.getPlayer().isSneaking()) return;

        Player player = event.getPlayer();
        Player victim = (Player) event.getRightClicked();
        Profile profile = Profiles.get(player);

        if (!Profiles.get(victim).isParticipating()) {
            if (PickpocketConfiguration.isShowStatusOnInteractEnabled())
                player.sendMessage(ChatColor.RED + "That player has pick-pocketing disabled.");
            return;
        }

        if (!profile.isParticipating()) {
            if (PickpocketConfiguration.isShowStatusOnInteractEnabled())
                player.sendMessage(ChatColor.RED + "You have pick-pocketing disabled.");
            return;
        }

        if (!pickpocketPlugin.getCooldowns().containsKey(player)) {
            final int numberOfRandomItems = 4;
            Menu rummageMenu = new Menu("Rummage", 4);
            rummageMenu.fill(new UniFill(Material.WHITE_STAINED_GLASS_PANE));
            MenuItem rummageButton = new MenuItem("Keep rummaging...", new ItemStack(Material.CHEST));
            rummageButton.addListener(clickEvent -> {
                populateRummageMenu(rummageMenu, profile, victim, numberOfRandomItems);
                rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);
                rummageMenu.update();
                // 1/5 chance that the player will get caught while rummaging.
                if (Math.random() < PickpocketConfiguration.getCaughtChance()) {
                    victim.sendMessage(ChatColor.RED + "You feel something touch your side.");

                    // Close the rummage inventory
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "They notice.");
                }

                // Play a sound when rummaging
                player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1.0f, 0.5f);
            });
            populateRummageMenu(rummageMenu, profile, victim, numberOfRandomItems);
            rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);
            profile.setRummaging(true);
            rummageMenu.show(player);
        } else {
            player.sendMessage(ChatColor.RED + "You must wait " + pickpocketPlugin.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
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
                profile.getPlayer().closeInventory();
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
}
