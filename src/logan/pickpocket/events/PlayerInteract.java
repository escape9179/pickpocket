package logan.pickpocket.events;

import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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

        if (!(event.getRightClicked() instanceof Player) || !event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        Player  player  = event.getPlayer();
        Profile profile = Profiles.get(player);

        if (!pickpocketPlugin.getCooldowns().containsKey(player))
        {
            Player entity = (Player) event.getRightClicked();
            Menu   menu   = new Menu("Rummaging", 4);

            // Pick 4 random items from the victims inventory
            // and place them into the pickpocketing inventory
            for (int i = 0; i < 4; i++)
            {
                final int inventorySize = entity.getInventory().getStorageContents().length;
                int       randomSlot    = 0;
                ItemStack randomItem    = null;

                while (randomItem == null)
                {
                    randomSlot = (int) (Math.random() * inventorySize);
                    randomItem = entity.getInventory().getStorageContents()[randomSlot];
                }

                menu.addItem(randomSlot, new MenuItem(randomItem));
            }

            menu.show(player);

            profile.setStealing(entity);
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You must wait " + pickpocketPlugin.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
        }
    }
}
