package logan.pickpocket.user

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.ThiefProfile
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
    var isAdmin
        get() = playerConfiguration.isAdmin
        set(value) { playerConfiguration.isAdmin = value }
    var isBypassing
        get() = playerConfiguration.isBypassing
        set(value) { playerConfiguration.isBypassing = value }
    var isExempt
        get() = playerConfiguration.isExempt
        set(value) { playerConfiguration.isExempt = value }
    var isParticipating
        get() = playerConfiguration.isParticipating
        set(value) { playerConfiguration.isParticipating = value }
    var currentMinigame: Minigame? = null
    val playerConfiguration =
        PlayerConfiguration("${PickpocketPlugin.instance.dataFolder}/players/", "$uuid.yml")
    var thiefProfile
        get() = findThiefProfile()
        set(value) { playerConfiguration.thiefProfile = value?.name ?: "default" }
    var steals
        get() = playerConfiguration.stealCount
        set(value) { playerConfiguration.stealCount = value }

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

    private fun findThiefProfile(): ThiefProfile? {
        return PickpocketPlugin.profileConfiguration.loadThiefProfiles().find { bukkitPlayer!!.hasPermission("pickpocket.profile.thief.${it.name}") }
            ?: PickpocketPlugin.profileConfiguration.loadThiefProfiles().find { thiefProfile?.equals(it) ?: return null }
    }

    fun assignThiefProfile(name: String): Boolean {
        PickpocketPlugin.profileConfiguration.loadThiefProfile(name)?.run {
            thiefProfile = this
            return true
        } ?: return false
    }

    fun giveCooldown() {
        val thiefProfile = findThiefProfile() ?: return
        if (!playerConfiguration.isBypassing) PickpocketPlugin.addCooldown(
            bukkitPlayer!!,
            thiefProfile.cooldown
        ) else Unit
    }

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