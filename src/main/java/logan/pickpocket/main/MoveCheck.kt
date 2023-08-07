package logan.pickpocket.main

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.user.PickpocketUser
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class MoveCheck {

    companion object {
        private val playerLocationMap: MutableMap<UUID, Location> = HashMap()
        fun check(player: Player) {
            val previousLocation = playerLocationMap[player.uniqueId] ?: run {
                playerLocationMap[player.uniqueId] = player.location
                return
            }

            val currentLocation = player.location

            // If the player is standing on the same block don't do anything.
            if (previousLocation.blockX == currentLocation.blockX && previousLocation.blockY == currentLocation.blockY && previousLocation.blockZ == currentLocation.blockZ) {
                return
            } else {
                playerLocationMap[player.uniqueId] = currentLocation
            }
            val user = PickpocketUser.get(player)

            // Check if the player is a predator.
            if (user.isPredator) {
                val victimProfile = user.victim
                if (user.isPlayingMinigame) {
                    user.currentMinigame!!.stop()
                }
                if (user.isRummaging) {
                    user.bukkitPlayer!!.closeInventory()
                    user.isRummaging = false
                }
                player.sendMessage(MessageConfiguration.pickpocketOnMoveWarningMessage)
                user.victim = null
                victimProfile!!.predator = null
                return
            }

            // Check if the player is a victim.
            if (user.isVictim) {
                val predatorProfile = user.predator
                if (predatorProfile!!.isPlayingMinigame) {
                    predatorProfile.currentMinigame!!.stop()
                }
                if (predatorProfile.isRummaging) {
                    predatorProfile.bukkitPlayer!!.closeInventory()
                    predatorProfile.isRummaging = false
                }
                user.predator = null
                predatorProfile.victim = null
            }
        }
    }
}