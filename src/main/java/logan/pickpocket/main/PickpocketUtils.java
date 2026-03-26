package logan.pickpocket.main;

import logan.pickpocket.config.Config;
import org.bukkit.Material;

public final class PickpocketUtils {

    private PickpocketUtils() {
    }

    public static boolean isItemTypeDisabled(Material type) {
        return Config.getDisabledItems().contains(type.name().toLowerCase());
    }
}
