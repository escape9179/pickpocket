package logan.pickpocket.managers;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * Opens and identifies per-user pickpocket inventory blueprint editors.
 */
public final class PickpocketInventoryManager {

    public static final String PICKPOCKET_INVENTORY_TITLE = "Pickpocket Inventory";
    public static final int PICKPOCKET_INVENTORY_SIZE = 54;

    private PickpocketInventoryManager() {
    }

    /**
     * Opens the pickpocket inventory editor for a player.
     *
     * @param user blueprint owner
     */
    public static void openFor(PickpocketUser user) {
        Player player = user.getBukkitPlayer();
        if (player == null) {
            return;
        }
        PickpocketInventoryHolder holder = new PickpocketInventoryHolder(user.getUuid());
        Inventory inventory = Bukkit.createInventory(holder, PICKPOCKET_INVENTORY_SIZE, PICKPOCKET_INVENTORY_TITLE);
        inventory.setContents(user.getPickpocketInventorySnapshot());
        player.openInventory(inventory);
    }

    /**
     * @param inventory inventory instance
     * @return true when this inventory is the pickpocket blueprint editor
     */
    public static boolean isPickpocketInventory(Inventory inventory) {
        if (inventory == null) {
            return false;
        }
        return inventory.getHolder() instanceof PickpocketInventoryHolder;
    }

    /**
     * @param inventory pickpocket inventory
     * @return owning user UUID, or null
     */
    public static UUID getOwnerUuid(Inventory inventory) {
        if (!(inventory.getHolder() instanceof PickpocketInventoryHolder holder)) {
            return null;
        }
        return holder.ownerUuid;
    }

    private static final class PickpocketInventoryHolder implements InventoryHolder {
        private final UUID ownerUuid;

        private PickpocketInventoryHolder(UUID ownerUuid) {
            this.ownerUuid = ownerUuid;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
