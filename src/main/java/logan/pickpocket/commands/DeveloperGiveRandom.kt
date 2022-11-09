package logan.pickpocket.commands

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.util.sendMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.Random

class DeveloperGiveRandom : BasicCommand<Player>(
    "giverandom",
    "pickpocket.developer.giverandom",    //1..1,
    1..1,//listOf(String::class),
    arrayOf("gr"),
    target = SenderTarget.PLAYER,
    argTypes = listOf(String::class),
    usage = """
        /pickpocket developer giverandom <amount>
    """.trimIndent(),
    parentCommand = "developer"
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val maxMaterials = Material.values().size
        for (i in 0..args[1].toInt()) {
            val material = Material.values()[Random().nextInt(maxMaterials - 1)]
            sender.inventory.addItem(ItemStack(material, (Math.random() * material.maxStackSize - 1).toInt()))
        }
        sender.sendMessage("&eGiven random items.", true)

        return true
    }
}