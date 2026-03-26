package logan.pickpocket.listeners;

import com.earth2me.essentials.Essentials;
import com.palmergames.bukkit.towny.TownyAPI;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.config.Config;
import logan.pickpocket.main.PickpocketPlugin;
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
        if (!event.getPlayer().isSneaking()) return;
        if (!(event.getRightClicked() instanceof Player victim)) return;

        // If offHand is null then getHand() doesn't exist,
        // and if getHand() isn't offHand, we should return
        // so this code isn't run twice.
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        // Nothing will happen to players who don't have the pick-pocket use permission.
        if (!event.getPlayer().hasPermission(PickpocketPlugin.PICKPOCKET_USE)) {
            return;
        }

        /* AFK checks */
        if (PickpocketPlugin.isEssentialsPresent()) {
            Essentials essentials = (Essentials) PickpocketPlugin.getEssentials();

            /* Check if the victim is AFK */
            if (essentials.getUser(event.getRightClicked().getUniqueId()).isAfk()) {
                event.getPlayer().sendMessage(MessageConfiguration.getPlayerStealFromAfkMessage());
                return;
            }

            /* Check if the predator is AFK */
            if (essentials.getUser(event.getPlayer().getUniqueId()).isAfk()) {
                event.getPlayer().sendMessage(MessageConfiguration.getPlayerStealWhileAfk());
                return;
            }
        }

        /* Foreign town member check */
        if (PickpocketPlugin.isTownyPresent()) {
            if (!isTownMember(event.getPlayer()) && isTownMember(victim) && !Config.isForeignTownTheft()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot steal from players in their own town.");
                return;
            }

            /* Same town member check */
            if (isTownMember(event.getPlayer()) && isTownMember(victim) && !Config.isSameTownTheft()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot steal from your own town-folk!");
                return;
            }
        }

        PickpocketUser victimUser = PickpocketUser.get(victim);
        PickpocketUser profile = PickpocketUser.get(event.getPlayer());

        if (profile.findThiefProfile() == null) {
            event.getPlayer().sendMessage("You aren't allowed to pickpocket.");
            return;
        }

        if (!Config.isPickpocketingEnabled()) {
            if (Config.isStatusOnInteract())
                event.getPlayer().sendMessage(MessageConfiguration.getPickpocketDisabledMessage());
            return;
        }

        profile.doPickpocket(victimUser);
    }

    private boolean isTownMember(Player player) {
        var town = TownyAPI.getInstance().getTown(player.getLocation());
        return town != null && town.hasResident(player.getName());
    }
}
