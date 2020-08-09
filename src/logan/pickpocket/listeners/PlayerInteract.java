package logan.pickpocket.listeners;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteract implements Listener
{

    public PlayerInteract()
    {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event)
    {
        PickpocketPlugin pickpocketPlugin = PickpocketPlugin.getInstance();

        if (!(event.getRightClicked() instanceof Player) ||
                !event.getHand().equals(EquipmentSlot.OFF_HAND) ||
                !event.getPlayer().isSneaking()) return;

        Player  player  = event.getPlayer();
        Player  victim  = (Player) event.getRightClicked();
        Profile profile = Profiles.get(player);

        if (!Profiles.get(victim).isParticipating())
        {
            player.sendMessage(ChatColor.RED + "That player has pick-pocketing disabled.");
            return;
        }

        if (!profile.isParticipating())
        {
            player.sendMessage(ChatColor.RED + "You have pick-pocketing disabled.");
            return;
        }

        if (!pickpocketPlugin.getCooldowns().containsKey(player))
        {
            final int numberOfRandomItems = 4;

            showNewRummageMenu(player, victim, getRandomItemsFromPlayer(victim, numberOfRandomItems));

            profile.setRummaging(true);
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You must wait " + pickpocketPlugin.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
        }
    }

    private void showNewRummageMenu(Player player, Player victim, List<ItemStack> randomItems)
    {
        Menu     rummageMenu   = new Menu("Rummage", 4);
        MenuItem rummageButton = new MenuItem("Keep rummaging...", new ItemStack(Material.CHEST));

        rummageMenu.fill(new UniFill(Material.WHITE_STAINED_GLASS_PANE));

        rummageButton.addListener(clickEvent ->
        {
            player.closeInventory();
            showNewRummageMenu(player, victim, getRandomItemsFromPlayer(victim, 4));
        });
        rummageMenu.addItem(rummageMenu.getBottomRight(), rummageButton);

        for (int i = 0; i < randomItems.size(); i++)
        {
            MenuItem menuItem = new MenuItem(randomItems.get(i));
            menuItem.addListener(clickEvent ->
            {
                final ItemStack fillerItem      = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                final Inventory inventory       = rummageMenu.getInventory();
                final Profile   profile         = Profiles.get(player);
                final int       bottomRightSlot = rummageMenu.getBottomRight();

                inventory.setItem(bottomRightSlot, fillerItem);
                player.closeInventory();

                profile.setStealing(victim);
            });

            rummageMenu.addItem((int) (Math.random() * (rummageMenu.getSlots() - 9)), menuItem);
        }

        rummageMenu.show(player);

        // 1/5 chance that the player will get caught while rummaging.
        if (Math.random() < 0.1)
        {
            victim.sendMessage(ChatColor.RED + "You feel something touch your side.");

            // Close the rummage inventory
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "They notice.");
        }

        // Play a sound when rummaging
        player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1.0f, 0.5f);
    }

    private List<ItemStack> getRandomItemsFromPlayer(Player player, int numberOfItems)
    {
        final List<ItemStack> randomItemList  = new ArrayList<>();
        final ItemStack[]     storageContents = player.getInventory().getStorageContents();
        final int             inventorySize   = player.getInventory().getStorageContents().length;
        ItemStack             randomItem;
        int                   randomSlot;

        outer:
        for (int i = 0; i < numberOfItems; i++)
        {
            randomSlot = 9 + (int) (Math.random() * (inventorySize - 9));
            randomItem = storageContents[randomSlot];

            if (randomItem == null) continue;

            // Check if the item is banned
            for (String disabledItem : PickpocketPlugin.getDisabledItems())
            {
                Material disabledItemType = Material.getMaterial(disabledItem.toUpperCase());

                // This item is disabled. Skip this random item iteration.
                if (randomItem.getType().equals(disabledItemType)) continue outer;
            }

            randomItemList.add(randomItem);
        }

        return randomItemList;
    }
}
