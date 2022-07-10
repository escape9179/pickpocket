package logan.pickpocket.main

import logan.pickpocket.config.PickpocketConfiguration
import org.bukkit.Material

class PickpocketUtils {
    companion object {
        fun isItemTypeDisabled(type: Material) =
            PickpocketConfiguration.disabledItems.contains(type.name.lowercase())
    }
}