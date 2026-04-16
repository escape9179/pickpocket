package logan.pickpocket.managers;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * Opens and identifies per-user trap inventories.
 */
public final class TrapInventoryManager {

    public static final String TRAP_MENU_TITLE = "Traps";
    public static final int TRAP_MENU_SIZE = 54;

    private TrapInventoryManager() {
    }

    /**
     * Opens the trap inventory for a player.
     *
     * @param user trap owner
     */
    public static void openFor(PickpocketUser user) {
        Player player = user.getBukkitPlayer();
        if (player == null) {
            return;
        }
        TrapInventoryHolder holder = new TrapInventoryHolder(user.getUuid());
        Inventory inventory = Bukkit.createInventory(holder, TRAP_MENU_SIZE, TRAP_MENU_TITLE);
        inventory.setContents(user.getTrapContentsSnapshot());
        player.openInventory(inventory);
    }

    /**
     * @param inventory inventory instance
     * @return true when this inventory is a trap menu
     */
    public static boolean isTrapInventory(Inventory inventory) {
        if (inventory == null) {
            return false;
        }
        return inventory.getHolder() instanceof TrapInventoryHolder;
    }

    /**
     * @param inventory trap inventory
     * @return owning user UUID, or null
     */
    public static UUID getOwnerUuid(Inventory inventory) {
        if (!(inventory.getHolder() instanceof TrapInventoryHolder holder)) {
            return null;
        }
        return holder.ownerUuid;
    }

    private static final class TrapInventoryHolder implements InventoryHolder {
        private final UUID ownerUuid;

        private TrapInventoryHolder(UUID ownerUuid) {
            this.ownerUuid = ownerUuid;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
