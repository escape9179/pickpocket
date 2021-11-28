package logan.pickpocket.user

import logan.api.gui.Menu
import logan.api.gui.MenuItem
import logan.api.gui.MenuItemClickEvent
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.Profiles
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val maxTries = 5
private const val inventorySize = 36

class Minigame(val predatorUser: PickpocketUser, val victimUser: PickpocketUser, val item: ItemStack) {
    private lateinit var gameTimerTask: BukkitTask
    private val gui = Menu("Pick-pocketing ${victimUser.bukkitPlayer?.name}", inventorySize / 9)
    private val correctClicks = AtomicInteger(0)
    private val timesTried = AtomicInteger(0)
    private val clickedInTime = AtomicBoolean(false)

    private fun setupGUI(inventory: Inventory) {
        val menuItemMap = createMinigameMenuItems(inventory)
        menuItemMap.forEach(gui::addItem)
    }

    private fun showGUI() {
        val predator = predatorUser.bukkitPlayer
        predator?.closeInventory()
        gui.show(predator)
    }

    fun start(inventory: Inventory) {
        setupGUI(inventory)
        showGUI()
        predatorUser.isPlayingMinigame = true
        predatorUser.currentMinigame = this
        resetGameTimerRunnable()
    }

    fun stop() {
        gameTimerTask.cancel()
        reset()
        predatorUser.isPlayingMinigame = false
        predatorUser.bukkitPlayer?.closeInventory()
        victimUser.predator = null
        predatorUser.victim = null
        predatorUser.currentMinigame = null
    }

    private fun reset() {
        correctClicks.set(0)
        timesTried.set(0)
        clickedInTime.set(false)
    }

    private fun stealItem(thief: Player, victim: Player, item: ItemStack) {
        victim.inventory.setItem(victim.inventory.first(item), null)
        thief.inventory.addItem(item)
    }

    private fun getPercentageOfVictimBalance(victim: Player): Double {
        val economy = PickpocketPlugin.getEconomy()
        return economy.getBalance(victim) * PickpocketPlugin.getPickpocketConfiguration().moneyLostPercentage
    }

    private fun doMoneyTransaction(thief: Player, victim: Player, amountStolen: Double) {
        val economy = PickpocketPlugin.getEconomy()
        if (amountStolen > 0) {
            economy.withdrawPlayer(victim, amountStolen)
            economy.depositPlayer(thief, amountStolen)
            thief.sendMessage(MessageConfiguration.getMoneyAmountReceivedMessage(String.format("%.2f", amountStolen)))
        } else thief.sendMessage(MessageConfiguration.getNoMoneyReceivedMessage())
    }

    private fun isMoneyStealEnabled() =
        PickpocketPlugin.isVaultEnabled() && PickpocketPlugin.getPickpocketConfiguration().isMoneyLostEnabled

    private fun stealMoney() {
        if (isMoneyStealEnabled()) {
            val amountToSteal = getPercentageOfVictimBalance(victimUser.bukkitPlayer!!)
            doMoneyTransaction(predatorUser.bukkitPlayer!!, victimUser.bukkitPlayer, amountToSteal)
        }
    }

    private fun doPickpocketSuccess() {
        val predator = predatorUser.bukkitPlayer!!
        stealItem(predator, victimUser.bukkitPlayer!!, item)
        stealMoney()
        predator.playItemPickupSound()
        predator.sendMessage(MessageConfiguration.getPickpocketSuccessfulMessage())
        showAdminNotifications(true)
    }

    private fun doPickpocketFailure() {
        val predator = predatorUser.bukkitPlayer
        predator?.playBassSound()
        predator?.sendMessage(MessageConfiguration.getPickpocketUnsuccessfulMessage())
        showAdminNotifications(false)
    }

    private fun doGameLoop() {
        shuffleInventoryItems()
        if (timesTried.incrementAndGet() >= maxTries) {
            if (correctClicks.get() >= maxTries) doPickpocketSuccess() else doPickpocketFailure()
            stop()
            predatorUser.giveCooldown()
        } else {
            resetGameTimerRunnable()
            clickedInTime.set(false)
        }
    }

    private fun shuffleInventoryItems() {
        with(gui.inventory) {
            for (i in 0 until inventorySize) {
                if (getItem(i) == null) continue
                val randomSlot = (Math.random() * inventorySize).toInt()
                val temp = getItem(randomSlot) ?: run {
                    setItem(randomSlot, getItem(i))
                    setItem(i, null)
                    return@shuffleInventoryItems
                }
                setItem(randomSlot, getItem(i))
                setItem(i, temp)
            }
        }
    }

    private fun resetGameTimerRunnable() {
        val gameTimerRunnable = scheduleNewShuffleRunnable()
        gameTimerTask = gameTimerRunnable.runTaskLater(PickpocketPlugin.getInstance(), getMinigameRollRate())
    }

    private fun getMinigameRollRate() = PickpocketPlugin.getPickpocketConfiguration().minigameRollRate.toLong()

    private fun scheduleNewShuffleRunnable() = object : BukkitRunnable() {
        override fun run() {
            if (!clickedInTime.get()) {
                doGameLoop()
                predatorUser.bukkitPlayer?.playBassSound()
            }
        }
    }

    private fun createMinigameMenuItems(inventory: Inventory): Map<Int, MenuItem> {
        val menuItemMap = HashMap<Int, MenuItem>()
        for (i in 0 until inventorySize) {
            MenuItem(inventory.getItem(i) ?: ItemStack(Material.AIR, 1))
                .also { it.addListener(this::onMenuItemClick) }
                .also { menuItemMap[i] = it }
        }
        return menuItemMap
    }

    private fun onMenuItemClick(event: MenuItemClickEvent) {
        gameTimerTask.cancel()
        if (event.menuItem != null && event.menuItem.itemStack.type == item.type) {
            correctClicks.incrementAndGet()
            clickedInTime.set(true)
            event.player.playExperienceOrbPickupSound()
        } else event.player.playBassSound()
        doGameLoop()
    }

    private fun showAdminNotifications(success: Boolean) {
        Bukkit.getOnlinePlayers().forEach { player ->
            val profile = Profiles.get(player)
            if (profile.profileConfiguration.adminSectionValue) {
                player.sendMessage(
                    if (success)
                        MessageConfiguration.getPickpocketSuccessAdminNotificationMessage(
                            predatorUser.bukkitPlayer,
                            victimUser.bukkitPlayer
                        )
                    else MessageConfiguration.getPickpocketFailureAdminNotification(
                        predatorUser.bukkitPlayer,
                        victimUser.bukkitPlayer
                    )
                )
            }
        }
    }

    private fun Player.playItemPickupSound() {
        playSound(location, PickpocketPlugin.getAPIWrapper().soundEntityItemPickup, 1.0F, 1.0f)
    }

    private fun Player.playExperienceOrbPickupSound() {
        playSound(location, PickpocketPlugin.getAPIWrapper().soundEntityExperienceOrbPickup, 1.0f, 1.0f)
    }

    private fun Player.playBassSound() {
        playSound(location, PickpocketPlugin.getAPIWrapper().soundBlockNoteBlockBass, 1.0f, 1.0f)
    }
}