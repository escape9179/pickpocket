package logan.pickpocket.listeners;

import logan.pickpocket.config.MessageConfig;
import logan.pickpocket.inventory.PickpocketInventoryBlueprint;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.managers.PickpocketInventoryManager;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
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
 * Enforces pickpocket inventory editor rules and persistence.
 */
public final class PickpocketInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!PickpocketInventoryManager.isPickpocketInventory(topInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID ownerUuid = PickpocketInventoryManager.getOwnerUuid(topInventory);
        if (ownerUuid == null || !ownerUuid.equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        PickpocketUser owner = PickpocketUser.get(player);
        int maxTrapSlots = owner.resolveMaxTrapSlots();
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

        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= PickpocketInventoryManager.PICKPOCKET_INVENTORY_SIZE) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        PickpocketInventoryBlueprint.SlotKind currentKind = PickpocketInventoryBlueprint.kindFromItem(current);
        PickpocketInventoryBlueprint.SlotKind cursorKind = PickpocketInventoryBlueprint.kindFromItem(cursor);

        if (isMarkerExtractionAction(event.getAction())
                && isBlueprintMarker(currentKind)) {
            event.setCancelled(true);
            return;
        }

        if (currentKind == PickpocketInventoryBlueprint.SlotKind.STEALABLE
                && cursorKind == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM
                && isPlacementAction(event.getAction())) {
            handleTrapPlacementOnStealableSlot(
                    event, player, topInventory, rawSlot, cursor, maxTrapSlots, maxStackSize);
            return;
        }

        if (currentKind == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM
                && isTrapPickupAction(event.getAction())) {
            handleTrapRemovalToCursor(event, player, topInventory, rawSlot, current, cursor);
            return;
        }

        if (cursor != null && cursor.getType() != Material.AIR) {
            if (isBlueprintMarker(currentKind)
                    && cursorKind != PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
                event.setCancelled(true);
                return;
            }
            if (!isAllowedCursorOnSlot(cursor, current, rawSlot)) {
                event.setCancelled(true);
                return;
            }
            if (cursorKind == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
                if (maxTrapSlots <= 0) {
                    event.setCancelled(true);
                    return;
                }
                if (isPlacementAction(event.getAction())) {
                    int placedAmount = resolvePlacedAmount(
                            event.getAction(), cursor.getAmount(),
                            current == null || current.getType() == Material.AIR ? 0 : current.getAmount(),
                            maxStackSize);
                    if (placedAmount <= 0) {
                        return;
                    }
                    int existingAmount = current == null || current.getType() == Material.AIR ? 0 : current.getAmount();
                    int finalAmount = existingAmount + placedAmount;
                    if (PickpocketInventoryBlueprint.kindFromItem(current) == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM
                            && current != null
                            && current.getType() == cursor.getType()) {
                        if (finalAmount > maxStackSize) {
                            event.setCancelled(true);
                            return;
                        }
                    } else if (finalAmount > maxStackSize) {
                        event.setCancelled(true);
                        return;
                    }
                    boolean currentTrap = PickpocketInventoryBlueprint.kindFromItem(current)
                            == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM;
                    if (!currentTrap) {
                        int traps = countTrapSlots(topInventory);
                        if (traps >= maxTrapSlots) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!PickpocketInventoryManager.isPickpocketInventory(topInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID ownerUuid = PickpocketInventoryManager.getOwnerUuid(topInventory);
        if (ownerUuid == null || !ownerUuid.equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        PickpocketUser owner = PickpocketUser.get(player);
        int maxTrapSlots = owner.resolveMaxTrapSlots();
        int maxStackSize = owner.resolveMaxTrapStackSize();

        int projectedTraps = countTrapSlots(topInventory);
        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            int rawSlot = entry.getKey();
            if (rawSlot < 0 || rawSlot >= PickpocketInventoryManager.PICKPOCKET_INVENTORY_SIZE) {
                continue;
            }
            ItemStack oldStack = topInventory.getItem(rawSlot);
            ItemStack newStack = entry.getValue();
            if (PickpocketInventoryBlueprint.kindFromItem(oldStack) == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
                projectedTraps--;
            }
            if (newStack != null && newStack.getType() != Material.AIR
                    && PickpocketInventoryBlueprint.kindFromItem(newStack)
                    == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
                if (maxTrapSlots <= 0 || newStack.getAmount() > maxStackSize) {
                    event.setCancelled(true);
                    return;
                }
                PickpocketInventoryBlueprint.SlotKind oldKind = PickpocketInventoryBlueprint.kindFromItem(oldStack);
                if (oldKind != PickpocketInventoryBlueprint.SlotKind.STEALABLE
                        && oldKind != PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
                    event.setCancelled(true);
                    return;
                }
                projectedTraps++;
            }
        }
        if (projectedTraps > maxTrapSlots) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!PickpocketInventoryManager.isPickpocketInventory(topInventory)) {
            return;
        }
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        UUID ownerUuid = PickpocketInventoryManager.getOwnerUuid(topInventory);
        if (ownerUuid == null || !ownerUuid.equals(player.getUniqueId())) {
            return;
        }
        PickpocketUser owner = PickpocketUser.get(player);
        ItemStack[] raw = topInventory.getContents();
        ItemStack[] normalized = new ItemStack[PickpocketInventoryBlueprint.SIZE];
        for (int i = 0; i < PickpocketInventoryBlueprint.SIZE; i++) {
            ItemStack s = i < raw.length ? raw[i] : null;
            normalized[i] = PickpocketInventoryBlueprint.normalizeSlot(s);
        }
        String invalid = PickpocketInventoryBlueprint.validate(normalized, owner.resolveRequiredStealableSlotsForValidation());
        if (invalid != null) {
            Player online = owner.getBukkitPlayer();
            if (online != null) {
                online.sendMessage(MessageConfig.getInventoryLayoutInvalidMessage(invalid));
            }
            Bukkit.getScheduler().runTask(PickpocketPlugin.getInstance(),
                    () -> PickpocketInventoryManager.openFor(owner));
            return;
        }
        owner.setPickpocketInventoryContents(normalized);
        owner.save();
    }

    private static boolean isAllowedCursorOnSlot(ItemStack cursor, ItemStack current, int rawSlot) {
        PickpocketInventoryBlueprint.SlotKind cursorKind = PickpocketInventoryBlueprint.kindFromItem(cursor);
        if (cursorKind != PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
            return true;
        }
        PickpocketInventoryBlueprint.SlotKind slotKind = PickpocketInventoryBlueprint.kindFromItem(current);
        return slotKind == PickpocketInventoryBlueprint.SlotKind.STEALABLE
                || slotKind == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM;
    }

    private static int countTrapSlots(Inventory inventory) {
        int n = 0;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < PickpocketInventoryManager.PICKPOCKET_INVENTORY_SIZE && i < contents.length; i++) {
            if (PickpocketInventoryBlueprint.kindFromItem(contents[i])
                    == PickpocketInventoryBlueprint.SlotKind.TRAP_ITEM) {
                n++;
            }
        }
        return n;
    }

    private static boolean isBlueprintMarker(PickpocketInventoryBlueprint.SlotKind kind) {
        return kind == PickpocketInventoryBlueprint.SlotKind.EMPTY
                || kind == PickpocketInventoryBlueprint.SlotKind.STEALABLE
                || kind == PickpocketInventoryBlueprint.SlotKind.HINT;
    }

    private static boolean isMarkerExtractionAction(InventoryAction action) {
        return action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME
                || action == InventoryAction.DROP_ALL_SLOT
                || action == InventoryAction.DROP_ONE_SLOT
                || action == InventoryAction.CLONE_STACK;
    }

    private static boolean isTrapPickupAction(InventoryAction action) {
        return action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME;
    }

    private void handleTrapPlacementOnStealableSlot(
            InventoryClickEvent event,
            Player player,
            Inventory topInventory,
            int rawSlot,
            ItemStack cursor,
            int maxTrapSlots,
            int maxStackSize) {
        if (maxTrapSlots <= 0) {
            event.setCancelled(true);
            return;
        }
        int placedAmount = resolvePlacedAmount(event.getAction(), cursor.getAmount(), 0, maxStackSize);
        if (placedAmount <= 0 || placedAmount > maxStackSize) {
            event.setCancelled(true);
            return;
        }
        int traps = countTrapSlots(topInventory);
        if (traps >= maxTrapSlots) {
            event.setCancelled(true);
            return;
        }

        ItemStack trapStack = cursor.clone();
        trapStack.setAmount(placedAmount);
        topInventory.setItem(rawSlot, trapStack);

        int remaining = cursor.getAmount() - placedAmount;
        if (remaining <= 0) {
            player.setItemOnCursor(new ItemStack(Material.AIR));
        } else {
            ItemStack remainder = cursor.clone();
            remainder.setAmount(remaining);
            player.setItemOnCursor(remainder);
        }
        event.setCancelled(true);
    }

    private void handleTrapRemovalToCursor(
            InventoryClickEvent event,
            Player player,
            Inventory topInventory,
            int rawSlot,
            ItemStack trapStack,
            ItemStack cursor) {
        if (trapStack == null || trapStack.getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }
        if (cursor != null && cursor.getType() != Material.AIR) {
            event.setCancelled(true);
            return;
        }

        player.setItemOnCursor(trapStack.clone());
        topInventory.setItem(rawSlot, new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
        event.setCancelled(true);
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
}
