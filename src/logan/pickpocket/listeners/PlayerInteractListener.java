package logan.pickpocket.listeners;

import com.earth2me.essentials.Essentials;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteractListener implements Listener {

    public PlayerInteractListener() {
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

        // Nothing will happen to players who don't have the pick-pocket use permission.
        if (!player.hasPermission(PickpocketPlugin.PICKPOCKET_USE)) {
            return;
        }

        /* AFK checks */
        if (PickpocketPlugin.isEssentialsPresent()) {
            Essentials essentials = PickpocketPlugin.getEssentials();

            /* Check if the victim is AFK */
            if (essentials.getUser(event.getRightClicked().getUniqueId()).isAfk()) {
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PLAYER_STEAL_FROM_AFK));
                return;
            }

            /* Check if the predator is AFK */
            if (essentials.getUser(event.getPlayer().getUniqueId()).isAfk()) {
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PLAYER_STEAL_WHILE_AFK));
                return;
            }
        }

        /* Foreign town member check */
        if (PickpocketPlugin.isTownyPresent()) {
            if (!isTownMember(player) && isTownMember(victim) && !PickpocketPlugin.getPickpocketConfiguration().isForeignTownTheftEnabled()) {
                player.sendMessage(ChatColor.RED + "You cannot steal from players in their own town.");
                return;
            }

            /* Same town member check */
            if (isTownMember(player) && isTownMember(victim) && !PickpocketPlugin.getPickpocketConfiguration().isSameTownTheftEnabled()) {
                player.sendMessage(ChatColor.RED + "You cannot steal from your own town-folk!");
                return;
            }
        }

        PickpocketUser victimUser = Profiles.get(victim);
        PickpocketUser profile = Profiles.get(player);

        if (!victimUser.isParticipating()) {
            if (PickpocketPlugin.getPickpocketConfiguration().isShowStatusOnInteractEnabled())
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_DISABLED_OTHER_KEY));
            return;
        }

        if (!profile.isParticipating()) {
            if (PickpocketPlugin.getPickpocketConfiguration().isShowStatusOnInteractEnabled())
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_DISABLED_KEY));
            return;
        }
        profile.performPickpocket(victimUser);
    }

    private static boolean isTownMember(Player player) {
        Town town = TownyAPI.getInstance().getTown(player.getLocation());
        return town != null && town.hasResident(player.getName());
    }
}
