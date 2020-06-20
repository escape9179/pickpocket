package logan.pickpocket.events;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profile;
import logan.pickpocket.main.Profiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteract implements Listener {

    private Pickpocket pickpocket;

    public PlayerInteract(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
        pickpocket.getServer().getPluginManager().registerEvents(this, pickpocket);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player player = event.getPlayer();
        Profile profile = Profiles.get(player, pickpocket.getProfiles());


        if (!pickpocket.getCooldowns().containsKey(player) && !profile.canCooldownBypass()) {
            Player entity = (Player) event.getRightClicked();
            player.openInventory(entity.getInventory());
            profile.setStealing(entity);
            pickpocket.addCooldown(player);
            return;
        }

        if (pickpocket.getCooldowns().containsKey(player) && !profile.canCooldownBypass()) {
            player.sendMessage(ChatColor.RED + "You must wait " + pickpocket.getCooldowns().get(player) + " seconds before attempting another pickpocket.");
            return;
        }

        Player entity = (Player) event.getRightClicked();
        player.openInventory(entity.getInventory());
        profile.setStealing(entity);
    }
}
