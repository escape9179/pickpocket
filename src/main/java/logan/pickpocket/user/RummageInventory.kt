package logan.pickpocket.user

import logan.api.gui.MenuItem
import logan.api.gui.PlayerInventoryMenu
import logan.api.util.getRandomItemFromMainInventory
import logan.pickpocket.config.MessageConfiguration
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.main.PickpocketUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.atomic.AtomicInteger

class RummageInventory(private val victim: PickpocketUser) {
    private val rummageButton: MenuItem
    private val menu: PlayerInventoryMenu =
        PlayerInventoryMenu(menuTitle, 4)

    fun show(predator: PickpocketUser) {
        predator.victim = victim
        val thiefProfile = predator.findThiefProfile()!!
        populateRummageMenu()
        menu.addItem(menu.bottomRight, rummageButton)
        menu.show(predator.bukkitPlayer)
    }

    private fun populateRummageMenu() {
        menu.clear()
        val randomItems = randomItemsFromPlayer
        for (randomItem in randomItems) {
            val randomSlot = (Math.random() * (menu.size - 9)).toInt()
            val menuItem = MenuItem(randomItem)
            menuItem.addListener { menuItemClickEvent ->
                val predator = victim.predator
                predator!!.isRummaging = false
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
    }

    private val randomItemsFromPlayer: List<ItemStack>
        get() {
            val randomItemList: MutableList<ItemStack> = ArrayList()
            var randomItem: ItemStack?
            outer@ for (i in 0 until victim.predator!!.findThiefProfile()!!.maxRummageItems) {
                randomItem = victim.bukkitPlayer!!.getRandomItemFromMainInventory()
                if (randomItem == null) continue
                // This item is disabled. Skip this random item iteration.
                if (PickpocketUtils.isItemTypeDisabled(randomItem.type)) continue@outer
                randomItemList.add(randomItem)
            }
            return randomItemList
        }

    companion object {
        private const val menuTitle = "Rummage"
        private const val rummageButtonText = "Keep rummaging..."
    }

    init {
        rummageButton = MenuItem(rummageButtonText, ItemStack(Material.CHEST))
        rummageButton.addListener {
            val predator = victim.predator
            populateRummageMenu()
            predator!!.playRummageSound()
            victim.playRummageSound()
        }
    }
}