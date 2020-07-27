package logan.pickpocket.events;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteract implements Listener {

    public PlayerInteract() {
        Pickpocket.registerListener(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player player = event.getPlayer();
        Profile profile = Profiles.get(player);
        if (profile.getProfileConfiguration().getBypassSectionValue());
        else if (!pickpocket.getCooldowns().containsKey(player)) {
            Player entity = (Player) event.getRightClicked();
            player.openInventory(entity.getInventory());
            profile.setStealing(entity);
            pickpocket.addCooldown(player);
        }
        else {
            player.sendMessage(ChatColor.RED + "You must wait " + pickpocket.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
        }
    }
}
