package logan.pickpocket.user

import logan.pickpocket.commands.ThiefProfile
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

class PickpocketUser(val uuid: UUID) {
    val bukkitPlayer
        get() = Bukkit.getPlayer(uuid)
    var victim: PickpocketUser? = null
    var predator: PickpocketUser? = null
        set(value) {
            field = value
            if (lastPredator == null) lastPredator = field
        }
    var lastPredator: PickpocketUser? = null
    val isPredator
        get() = victim != null
    val isVictim
        get() = predator != null
    var isPlayingMinigame = false
    var isRummaging = false
    var openRummageInventory: RummageInventory? = null
    var isParticipating = true
        set(value) {
            field = value
            profileConfiguration.setParticipatingSection(value)
        }
    var currentMinigame: Minigame? = null
    val profileConfiguration =
        ProfileConfiguration("${PickpocketPlugin.instance.dataFolder}/players/", "$uuid.yml")
    var steals = 0

    fun doPickpocket(victim: PickpocketUser) {
        when {
            !WorldGuardUtil.isPickpocketingAllowed(bukkitPlayer!!) -> {
                bukkitPlayer!!.sendMessage(MessageConfiguration.pickpocketRegionDisallowMessage)
            }
            isCoolingDown() -> bukkitPlayer!!.sendMessage(
                MessageConfiguration.getCooldownNoticeMessage(PickpocketPlugin.getCooldowns()[bukkitPlayer].toString())
            )
            else -> {
                this.victim = victim
                victim.predator = this
                openRummageInventory = RummageInventory(victim)
                openRummageInventory?.show(this)
                isRummaging = true
            }
        }
    }

    fun findThiefProfile(): ThiefProfile {
        TODO()
    }

    fun giveCooldown() =
        if (!profileConfiguration.bypassSectionValue) PickpocketPlugin.addCooldown(
            bukkitPlayer!!,
            findThiefProfile().cooldown
        ) else Unit

    private fun isCoolingDown() = PickpocketPlugin.getCooldowns().containsKey(bukkitPlayer)

    fun sendMessage(message: String, vararg args: Any) {
        bukkitPlayer?.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)))
    }

    fun playRummageSound() {
        with(bukkitPlayer) {
            this?.playSound(
                location,
                Sound.BLOCK_SNOW_STEP,
                1.0f,
                0.5f
            )
        }
    }

    companion object {
        fun get(player: Player): PickpocketUser {
            for (user in PickpocketPlugin.users) {
                if (user.uuid == player.uniqueId) {
                    return user
                }
            }
            val user = PickpocketUser(player.uniqueId)
            PickpocketPlugin.addProfile(user)
            return user
        }
    }
}