package logan.pickpocket.events;

import logan.pickpocket.main.PickPocket;
import logan.pickpocket.main.Profile;
import logan.pickpocket.main.ProfileHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteract implements Listener {

    private PickPocket pickPocket;

    public PlayerInteract(PickPocket pickPocket) {
        this.pickPocket = pickPocket;
        pickPocket.getServer().getPluginManager().registerEvents(this, pickPocket);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player player = event.getPlayer();
        if (pickPocket.getCooldowns().containsKey(player)) {
            player.sendMessage(ChatColor.RED + "You must wait " + pickPocket.getCooldowns().get(player) + " more seconds before attempting another pickpocket.");
            return;
        } else if(!player.hasPermission(pickPocket.pickpocketBypassCooldown)) pickPocket.addCooldown(player);
        Player entity = (Player) event.getRightClicked();
        player.openInventory(entity.getInventory());
        Profile profile = ProfileHelper.getLoadedProfile(player, pickPocket.getProfiles());
        profile.setStealing(entity);
    }
}
