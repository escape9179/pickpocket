package logan.pickpocket.main

import logan.pickpocket.config.Config
import org.bukkit.Material

class PickpocketUtils {
    companion object {
        fun isItemTypeDisabled(type: Material) =
            Config.disabledItems.contains(type.name.lowercase())
    }
}