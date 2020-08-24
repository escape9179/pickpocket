package logan.pickpocket.listeners;

import logan.config.MessageConfiguration;
import logan.config.PickpocketConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
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

        if (!(event.getRightClicked() instanceof Player) ||
                !event.getPlayer().isSneaking()) return;

        // The interact event is fired for both hands. This will prevent it from
        // getting fired twice using Bukkit 1.9+.
        EquipmentSlot offHand = null;
        try {
            offHand = EquipmentSlot.valueOf("OFF_HAND");
        } catch (IllegalArgumentException e) {
            // The Bukkit API version being used is below 1.9, so
            // we don't have to worry about it being fired twice.
        }

        // If offHand is null then getHand() doesn't exist,
        // and if getHand() isn't offHand, we should return
        // so this code isn't run twice.
        if (offHand != null && event.getHand() != offHand) return;

        Player player = event.getPlayer();
        Player victim = (Player) event.getRightClicked();
        Profile profile = Profiles.get(player);

        if (!Profiles.get(victim).isParticipating()) {
            if (PickpocketConfiguration.isShowStatusOnInteractEnabled())
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_DISABLED_OTHER_KEY));
            return;
        }

        if (!profile.isParticipating()) {
            if (PickpocketConfiguration.isShowStatusOnInteractEnabled())
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_DISABLED_KEY));
            return;
        }
        profile.performPickpocket(victim);
    }
}
