package logan.pickpocket.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import logan.pickpocket.config.Config;
import logan.pickpocket.inventory.PickpocketInventoryBlueprint;

/**
 * Opens and identifies the global default pickpocket layout editor.
 */
public final class DefaultPickpocketInventoryManager {

    public static final String TITLE = "Pickpocket Default Layout";

    private DefaultPickpocketInventoryManager() {
    }

    public static void openFor(Player player) {
        if (player == null) {
            return;
        }
        DefaultPickpocketInventoryHolder holder = new DefaultPickpocketInventoryHolder(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, PickpocketInventoryBlueprint.SIZE, TITLE);
        inventory.setContents(Config.getDefaultPickpocketInventoryLayoutSnapshot());
        player.openInventory(inventory);
    }

    public static boolean isDefaultPickpocketInventory(Inventory inventory) {
        if (inventory == null) {
            return false;
        }
        return inventory.getHolder() instanceof DefaultPickpocketInventoryHolder;
    }

    public static UUID getEditorUuid(Inventory inventory) {
        if (!(inventory.getHolder() instanceof DefaultPickpocketInventoryHolder holder)) {
            return null;
        }
        return holder.editorUuid;
    }

    private static final class DefaultPickpocketInventoryHolder implements InventoryHolder {
        private final UUID editorUuid;

        private DefaultPickpocketInventoryHolder(UUID editorUuid) {
            this.editorUuid = editorUuid;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
