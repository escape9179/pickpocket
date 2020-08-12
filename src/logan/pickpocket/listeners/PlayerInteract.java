package logan.pickpocket.listeners;

import logan.config.PickpocketConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

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
        profile.pickpocket(victim);
    }
}
