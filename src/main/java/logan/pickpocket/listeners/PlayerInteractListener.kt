package logan.pickpocket.listeners

import com.earth2me.essentials.Essentials
import com.palmergames.bukkit.towny.TownyAPI
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.config.PickpocketConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.user.PickpocketUser
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * Created by Tre on 12/28/2015.
 */
class PlayerInteractListener : Listener {

    init {
        PickpocketPlugin.registerListener(this)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEntityEvent) {

        if (!event.player.isSneaking) return
        val victim = event.rightClicked as? Player ?: return

        // If offHand is null then getHand() doesn't exist,
        // and if getHand() isn't offHand, we should return
        // so this code isn't run twice.
        if (event.hand == EquipmentSlot.OFF_HAND) return

        // Nothing will happen to players who don't have the pick-pocket use permission.
        if (!event.player.hasPermission(PickpocketPlugin.PICKPOCKET_USE)) {
            return
        }

        /* AFK checks */
        if (PickpocketPlugin.isEssentialsPresent) {
            val essentials = PickpocketPlugin.essentials as Essentials

            /* Check if the victim is AFK */
            if (essentials.getUser(event.rightClicked.uniqueId).isAfk) {
                event.player.sendMessage(MessageConfiguration.playerStealFromAfkMessage)
                return
            }

            /* Check if the predator is AFK */
            if (essentials.getUser(event.player.uniqueId).isAfk) {
                event.player.sendMessage(MessageConfiguration.playerStealWhileAfk)
                return
            }
        }

        /* Foreign town member check */
        if (PickpocketPlugin.isTownyPresent) {
            if (!isTownMember(event.player) && isTownMember(victim) && !PickpocketConfiguration.foreignTownTheft) {
                event.player.sendMessage("${ChatColor.RED}You cannot steal from players in their own town.")
                return
            }

            /* Same town member check */
            if (isTownMember(event.player) && isTownMember(victim) && !PickpocketConfiguration.sameTownTheft) {
                event.player.sendMessage("${ChatColor.RED}You cannot steal from your own town-folk!")
                return
            }
        }

        val victimUser = PickpocketUser.get(victim)
        val profile = PickpocketUser.get(event.player)

        if (profile.findThiefProfile() == null) {
            event.player.sendMessage("You aren't allowed to pickpocket.")
            return
        }

        if (!victimUser.isParticipating) {
            if (PickpocketConfiguration.statusOnInteract)
                event.player.sendMessage(MessageConfiguration.pickpocketDisabledOtherMessage)
            return
        }

        if (!profile.isParticipating) {
            if (PickpocketConfiguration.statusOnInteract)
                event.player.sendMessage(MessageConfiguration.pickpocketDisabledMessage)
            return
        }

        profile.doPickpocket(victimUser)
    }

    private fun isTownMember(player: Player): Boolean {
        val town = TownyAPI.getInstance().getTown(player.location)
        return town != null && town.hasResident(player.name)
    }
}
