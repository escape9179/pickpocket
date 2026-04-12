package logan.pickpocket.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.earth2me.essentials.Essentials;
import com.palmergames.bukkit.towny.TownyAPI;

import logan.pickpocket.config.Config;
import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.managers.PickpocketSessionManager;
import logan.pickpocket.user.PickpocketUser;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {

        if (!event.getPlayer().isSneaking()) {
            return;
        }
        if (!(event.getRightClicked() instanceof Player victim)) {
            return;
        }

        // If offHand is null then getHand() doesn't exist,
        // and if getHand() isn't offHand, we should return
        // so this code isn't run twice.
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        // Nothing will happen to players who don't have the pick-pocket use permission.
        if (!event.getPlayer().hasPermission(PickpocketPlugin.PICKPOCKET_USE)) {
            return;
        }

        /* AFK checks */
        if (logan.pickpocket.hooks.EssentialsHook.isEssentialsPresent()) {
            Essentials essentials = logan.pickpocket.hooks.EssentialsHook.getEssentials();

            /* Check if the victim is AFK */
            if (essentials.getUser(event.getRightClicked().getUniqueId()).isAfk()) {
                event.getPlayer().sendMessage(MessageConfig.getPlayerStealFromAfkMessage());
                return;
            }

            /* Check if the predator is AFK */
            if (essentials.getUser(event.getPlayer().getUniqueId()).isAfk()) {
                event.getPlayer().sendMessage(MessageConfig.getPlayerStealWhileAfk());
                return;
            }
        }

        /* Foreign town member check */
        if (logan.pickpocket.hooks.TownyHook.isTownyPresent()) {
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
        PickpocketUser thiefUser = PickpocketUser.get(event.getPlayer());

        if (!Config.isPickpocketingEnabled()) {
            if (Config.isStatusOnInteract())
                event.getPlayer().sendMessage(MessageConfig.getPickpocketDisabledMessage());
            return;
        }

        if (PickpocketSessionManager.hasUserInSession(thiefUser)) {
            event.getPlayer().sendMessage(MessageConfig.getAlreadyInSessionMessage());
            return;
        }
        
        thiefUser.doPickpocket(victimUser);
    }

    private boolean isTownMember(Player player) {
        var town = TownyAPI.getInstance().getTown(player.getLocation());
        return town != null && town.hasResident(player.getName());
    }
}
