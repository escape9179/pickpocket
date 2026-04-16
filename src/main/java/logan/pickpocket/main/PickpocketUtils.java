package logan.pickpocket.main;

import logan.pickpocket.config.Config;
import org.bukkit.Material;

/**
 * Shared utility helpers for pickpocket rules.
 */
public final class PickpocketUtils {

    private PickpocketUtils() {
    }

    /**
     * @param type item material
     * @return true when item type is disallowed by config
     */
    public static boolean isItemTypeDisabled(Material type) {
        return Config.getDisabledItems().contains(type.name().toLowerCase());
    }
}
