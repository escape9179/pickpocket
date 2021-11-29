package logan.pickpocket.main

import logan.pickpocket.config.MessageConfiguration
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class MoveCheck {

    companion object {
        private val playerLocationMap: MutableMap<UUID, Location> = HashMap()
        fun check(player: Player) {
            val previousLocation = playerLocationMap[player.uniqueId]
            val currentLocation = player.location
            if (previousLocation == null) {
                playerLocationMap[player.uniqueId] = currentLocation
                return
            }

            // If the player is standing on the same block don't do anything.
            if (previousLocation.blockX == currentLocation.blockX && previousLocation.blockY == currentLocation.blockY && previousLocation.blockZ == currentLocation.blockZ) {
                return
            } else {
                playerLocationMap[player.uniqueId] = currentLocation
            }
            val playerProfile = Profiles.get(player)

            // Check if the player is a predator.
            if (playerProfile.isPredator) {
                val victimProfile = playerProfile.victim
                if (playerProfile.isPlayingMinigame) {
                    playerProfile.currentMinigame!!.stop()
                }
                if (playerProfile.isRummaging) {
                    playerProfile.bukkitPlayer!!.closeInventory()
                    playerProfile.isRummaging = false
                }
                player.sendMessage(MessageConfiguration.getPickpocketOnMoveWarningMessage())
                playerProfile.victim = null
                victimProfile!!.predator = null
                return
            }

            // Check if the player is a victim.
            if (playerProfile.isVictim) {
                val predatorProfile = playerProfile.predator
                if (predatorProfile!!.isPlayingMinigame) {
                    predatorProfile.currentMinigame!!.stop()
                }
                if (predatorProfile.isRummaging) {
                    predatorProfile.bukkitPlayer!!.closeInventory()
                    predatorProfile.isRummaging = false
                }
                playerProfile.lastPredator!!.sendMessage(MessageConfiguration.getPickpocketOnMoveOtherWarningMessage())
                playerProfile.predator = null
                predatorProfile.victim = null
            }
        }
    }
}