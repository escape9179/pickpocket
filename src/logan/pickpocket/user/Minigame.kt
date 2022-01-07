package logan.pickpocket.user

import logan.api.gui.MenuItem
import logan.api.gui.MenuItemClickEvent
import logan.api.gui.PlayerInventoryMenu
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val maxTries = 5
private const val inventorySize = 36

class Minigame(val predatorUser: PickpocketUser, private val victimUser: PickpocketUser, private val item: ItemStack) {
    private lateinit var gameTimerTask: BukkitTask
    private val gui = PlayerInventoryMenu(
        "Pick-pocketing ${victimUser.bukkitPlayer?.name}",
        inventorySize / 9
    )
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
        thief.inventory.addItem(item).forEach { entry ->
            thief.world.dropItemNaturally(thief.location, entry.value)
        }
    }

    private fun getPercentageOfVictimBalance(victim: Player): Double {
        val economy = PickpocketPlugin.economy ?: return 0.0
        return economy.getBalance(victim) * PickpocketPlugin.pickpocketConfiguration.moneyPercentageToSteal
    }

    private fun doMoneyTransaction(thief: Player, victim: Player, amountStolen: Double) {
        val economy = PickpocketPlugin.economy ?: return
        if (amountStolen > 0) {
            economy.withdrawPlayer(victim, amountStolen)
            economy.depositPlayer(thief, amountStolen)
            thief.sendMessage(MessageConfiguration.getMoneyAmountReceivedMessage(String.format("%.2f", amountStolen)))
        } else thief.sendMessage(MessageConfiguration.noMoneyReceivedMessage)
    }

    private fun isMoneyStealEnabled() =
        PickpocketPlugin.isVaultEnabled && PickpocketPlugin.pickpocketConfiguration.moneyCanBeStolen

    private fun stealMoney() {
        if (isMoneyStealEnabled()) {
            val amountToSteal = getPercentageOfVictimBalance(victimUser.bukkitPlayer!!)
            doMoneyTransaction(predatorUser.bukkitPlayer!!, victimUser.bukkitPlayer!!, amountToSteal)
        }
    }

    private fun doPickpocketSuccess() {
        val predator = predatorUser.bukkitPlayer!!
        stealItem(predator, victimUser.bukkitPlayer!!, item)
        stealMoney()
        predator.playItemPickupSound()
        predatorUser.steals++
        predatorUser.playerConfiguration.setSteals(predatorUser.steals)
        predator.sendMessage(MessageConfiguration.pickpocketSuccessfulMessage)
        showAdminNotifications(true)
        predatorUser.steals++
        PickpocketPlugin.database?.updateUser(predatorUser)
    }

    private fun doPickpocketFailure() {
        val predator = predatorUser.bukkitPlayer
        predator?.playBassSound()
        predator?.sendMessage(MessageConfiguration.pickpocketUnsuccessfulMessage)
        victimUser.playRummageSound()
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
                val temp = getItem(randomSlot) ?: run { ItemStack(Material.AIR) }
                setItem(randomSlot, getItem(i))
                setItem(i, temp)
            }
        }
    }

    private fun resetGameTimerRunnable() {
        val gameTimerRunnable = scheduleNewShuffleRunnable()
        gameTimerTask = gameTimerRunnable.runTaskLater(PickpocketPlugin.instance, getMinigameRollRate())
    }

    private fun getMinigameRollRate() = predatorUser.findThiefProfile()!!.minigameRollRate

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
            val profile = PickpocketUser.get(player)
            if (profile.playerConfiguration.adminSectionValue) {
                player.sendMessage(
                    if (success)
                        MessageConfiguration.getPickpocketSuccessAdminNotificationMessage(
                            predatorUser.bukkitPlayer!!,
                            victimUser.bukkitPlayer!!
                        )
                    else MessageConfiguration.getPickpocketFailureAdminNotification(
                        predatorUser.bukkitPlayer!!,
                        victimUser.bukkitPlayer!!
                    )
                )
            }
        }
    }

    private fun Player.playItemPickupSound() {
        playSound(location, Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0f)
    }

    private fun Player.playExperienceOrbPickupSound() {
        playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
    }

    private fun Player.playBassSound() {
        playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f)
    }
}