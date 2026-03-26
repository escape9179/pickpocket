package logan.api.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerUtils {

    private PlayerUtils() {
    }

    public static ItemStack getRandomItemFromMainInventory(Player player) {
        int slot = (int) (Math.random() * (35 - 9) + 9);
        return player.getInventory().getItem(slot);
    }
}
