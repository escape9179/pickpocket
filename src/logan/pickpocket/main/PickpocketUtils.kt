package logan.pickpocket.main

import org.bukkit.Material

class PickpocketUtils {
    companion object {
        fun isItemTypeDisabled(type: Material) =
            PickpocketPlugin.pickpocketConfiguration.disabledItems.contains(type.name.lowercase())
    }
}