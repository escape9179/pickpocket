package logan.pickpocket.user

import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*

class PickpocketUser(val uuid: UUID) {
    val bukkitPlayer = Bukkit.getPlayer(uuid)
    var victim: PickpocketUser? = null
    var predator: PickpocketUser? = null
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
        ProfileConfiguration("${PickpocketPlugin.getInstance().dataFolder}/players/", "$uuid.yml")

    fun doPickpocket(victim: PickpocketUser) {
        if (!WorldGuardUtil.isPickpocketingAllowed(bukkitPlayer!!))
            bukkitPlayer.sendMessage(MessageConfiguration.getPickpocketRegionDisallowMessage())
        if (isCoolingDown()) bukkitPlayer.sendMessage(
            MessageConfiguration.getCooldownNoticeMessage(PickpocketPlugin.getCooldowns()[bukkitPlayer].toString())
        )
        else {
            openRummageInventory = RummageInventory(victim)
            openRummageInventory?.show(this)
            isRummaging = true
            this.victim = victim
            victim.predator = this
        }
    }

    fun giveCooldown() =
        if (!profileConfiguration.bypassSectionValue) PickpocketPlugin.addCooldown(bukkitPlayer) else Unit

    fun isCoolingDown() = PickpocketPlugin.getCooldowns().containsKey(bukkitPlayer)

    fun sendMessage(message: String, vararg args: Any) {
        bukkitPlayer?.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)))
    }

    fun playRummageSound() {
        with(bukkitPlayer) {
            this?.playSound(
                location,
                PickpocketPlugin.getAPIWrapper().soundBlockSnowStep,
                1.0f,
                0.5f
            )
        }
    }
}