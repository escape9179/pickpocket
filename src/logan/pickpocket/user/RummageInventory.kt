package logan.pickpocket.user

import logan.api.gui.Menu
import logan.api.gui.MenuItem
import logan.api.gui.fill.UniFill
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class RummageInventory(private val victim: PickpocketUser) {
    private var rummageTimerTask: BukkitTask? = null
    private val rummageButton: MenuItem
    private val menu: Menu = Menu(menuTitle, 4)
    fun show(predator: PickpocketUser) {
        predator.victim = victim
        populateRummageMenu()
        menu.addItem(menu.bottomRight, rummageButton)
        menu.show(predator.bukkitPlayer)

        // Start rummage timer
        rummageTimerTask = object : BukkitRunnable() {
            val tickCount = AtomicInteger(0)
            override fun run() {
                if (tickCount.getAndIncrement() >= ticksUntilNoticed) {
                    victim.playRummageSound()
                    // Close the rummage inventory
                    menu.close()
                    predator.sendMessage(MessageConfiguration.pickpocketNoticedWarningMessage)
                    if (!predator.profileConfiguration.bypassSectionValue) PickpocketPlugin.addCooldown(predator.bukkitPlayer!!)
                    rummageTimerTask!!.cancel()
                }
                with (predator.bukkitPlayer!!) {
                    playSound(location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                }
            }
        }.runTaskTimer(PickpocketPlugin.instance, rummageTimerTickRate.toLong(), rummageTimerTickRate.toLong())
    }

    private fun populateRummageMenu() {
        menu.clear()
        menu.fill(UniFill(Material.AIR))
        val randomItems = randomItemsFromPlayer
        for (randomItem in randomItems) {
            val randomSlot = (Math.random() * (menu.size - 9)).toInt()
            val menuItem = MenuItem(randomItem)
            menuItem.addListener { menuItemClickEvent ->
                val predator = victim.predator
                predator!!.isRummaging = false
                rummageTimerTask!!.cancel()
                val bottomRightSlot = menu.bottomRight
                menu.addItem(bottomRightSlot, MenuItem(ItemStack(Material.AIR)))
                menu.update()
                menu.close()
                if (victim.bukkitPlayer == null || !victim.bukkitPlayer!!.isOnline) predator.sendMessage(ChatColor.RED.toString() + "Player is no longer available.")
                val minigame =
                    Minigame(predator, victim, menuItemClickEvent.inventoryClickEvent.currentItem!!)
                minigame.start(menu.inventory)
            }
            menu.addItem(randomSlot, menuItem)
        }
        menu.addItem(menu.bottomRight, rummageButton)
        menu.update()
    }// This item is disabled. Skip this random item iteration.

    // Check if the item is banned
    private val randomItemsFromPlayer: List<ItemStack>
        get() {
            val randomItemList: MutableList<ItemStack> = ArrayList()
            val storageContents = victim.bukkitPlayer!!.inventory.storageContents
            val inventorySize = victim.bukkitPlayer!!.inventory.storageContents.size
            var randomItem: ItemStack?
            var randomSlot: Int
            outer@ for (i in 0 until randomItemCount) {
                randomSlot = 9 + (Math.random() * (inventorySize - 9)).toInt()
                randomItem = storageContents[randomSlot]
                if (randomItem == null) continue

                // Check if the item is banned
                for (disabledItem in PickpocketPlugin.pickpocketConfiguration.disabledItems) {
                    val disabledItemType = Material.getMaterial(disabledItem.uppercase(Locale.getDefault()))

                    // This item is disabled. Skip this random item iteration.
                    if (randomItem.type == disabledItemType) continue@outer
                }
                randomItemList.add(randomItem)
            }
            return randomItemList
        }

    fun close() {
        rummageTimerTask!!.cancel()
    }

    companion object {
        private const val menuTitle = "Rummage"
        private const val rummageButtonText = "Keep rummaging..."
        private const val randomItemCount = 4
        private const val rummageTimerTickRate = 20
        private const val ticksUntilNoticed = 4
    }

    init {
        menu.fill(UniFill(Material.AIR))
        rummageButton = MenuItem(rummageButtonText, ItemStack(Material.CHEST))
        rummageButton.addListener {
            val predator = victim.predator
            populateRummageMenu()
            predator!!.playRummageSound()
        }
    }
}