package logan.pickpocket.listeners;

import logan.pickpocket.managers.TrapInventoryManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * Enforces trap inventory interaction limits and persistence.
 */
public final class TrapInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!TrapInventoryManager.isTrapInventory(topInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID ownerUuid = TrapInventoryManager.getOwnerUuid(topInventory);
        if (ownerUuid == null || !ownerUuid.equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        PickpocketUser owner = PickpocketUser.get(player);
        int maxSlots = owner.resolveMaxTrapSlots();
        int maxStackSize = owner.resolveMaxTrapStackSize();

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() != null
                && event.getClickedInventory().equals(topInventory)
                && event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(topInventory)) {
            return;
        }

        if (isPlacementAction(event.getAction())) {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType() == Material.AIR) {
                return;
            }
            ItemStack existing = event.getCurrentItem();
            int existingAmount = existing == null || existing.getType() == Material.AIR ? 0 : existing.getAmount();
            int placedAmount = resolvePlacedAmount(event.getAction(), cursor.getAmount(), existingAmount, maxStackSize);
            if (placedAmount <= 0) {
                return;
            }
            int finalAmount = existingAmount + placedAmount;
            if (finalAmount > maxStackSize) {
                event.setCancelled(true);
                return;
            }
            if (existingAmount == 0 && countOccupiedSlots(topInventory) >= maxSlots) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!TrapInventoryManager.isTrapInventory(topInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID ownerUuid = TrapInventoryManager.getOwnerUuid(topInventory);
        if (ownerUuid == null || !ownerUuid.equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        PickpocketUser owner = PickpocketUser.get(player);
        int maxSlots = owner.resolveMaxTrapSlots();
        int maxStackSize = owner.resolveMaxTrapStackSize();

        int occupiedSlots = countOccupiedSlots(topInventory);
        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            int rawSlot = entry.getKey();
            if (rawSlot < 0 || rawSlot >= topInventory.getSize()) {
                continue;
            }
            ItemStack newStack = entry.getValue();
            if (newStack == null || newStack.getType() == Material.AIR) {
                continue;
            }
            if (newStack.getAmount() > maxStackSize) {
                event.setCancelled(true);
                return;
            }
            ItemStack existing = topInventory.getItem(rawSlot);
            boolean wasEmpty = existing == null || existing.getType() == Material.AIR;
            if (wasEmpty) {
                occupiedSlots++;
                if (occupiedSlots > maxSlots) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!TrapInventoryManager.isTrapInventory(topInventory)) {
            return;
        }
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        UUID ownerUuid = TrapInventoryManager.getOwnerUuid(topInventory);
        if (ownerUuid == null || !ownerUuid.equals(player.getUniqueId())) {
            return;
        }
        PickpocketUser owner = PickpocketUser.get(player);
        owner.setTrapContents(topInventory.getContents());
        owner.save();
    }

    private boolean isPlacementAction(InventoryAction action) {
        return action == InventoryAction.PLACE_ALL
                || action == InventoryAction.PLACE_ONE
                || action == InventoryAction.PLACE_SOME
                || action == InventoryAction.SWAP_WITH_CURSOR;
    }

    private int resolvePlacedAmount(InventoryAction action, int cursorAmount, int existingAmount, int maxStackSize) {
        if (action == InventoryAction.PLACE_ALL) {
            return cursorAmount;
        }
        if (action == InventoryAction.PLACE_ONE) {
            return 1;
        }
        if (action == InventoryAction.PLACE_SOME) {
            return Math.max(0, maxStackSize - existingAmount);
        }
        if (action == InventoryAction.SWAP_WITH_CURSOR) {
            return cursorAmount;
        }
        return 0;
    }

    private int countOccupiedSlots(Inventory inventory) {
        int occupied = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            occupied++;
        }
        return occupied;
    }
}
