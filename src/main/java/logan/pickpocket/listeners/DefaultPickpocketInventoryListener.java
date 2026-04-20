package logan.pickpocket.listeners;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import logan.pickpocket.config.Config;
import logan.pickpocket.inventory.PickpocketInventoryBlueprint;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.managers.DefaultPickpocketInventoryManager;

/**
 * Validates and persists edits to the global default pickpocket inventory layout.
 */
public final class DefaultPickpocketInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!DefaultPickpocketInventoryManager.isDefaultPickpocketInventory(topInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID editorUuid = DefaultPickpocketInventoryManager.getEditorUuid(topInventory);
        if (editorUuid == null || !editorUuid.equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
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
        if (rawSlot < 0 || rawSlot >= PickpocketInventoryBlueprint.SIZE) {
            return;
        }
        if (shouldCycleMarkerOnClick(event, event.getCursor(), event.getCurrentItem())) {
            Material nextMarker = PickpocketInventoryBlueprint.nextMarkerMaterial(event.getCurrentItem().getType());
            if (nextMarker != null) {
                topInventory.setItem(rawSlot, new ItemStack(nextMarker));
                event.setCancelled(true);
                return;
            }
        }
        if (!isValidMarker(event.getCursor())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!DefaultPickpocketInventoryManager.isDefaultPickpocketInventory(topInventory)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        UUID editorUuid = DefaultPickpocketInventoryManager.getEditorUuid(topInventory);
        if (editorUuid == null || !editorUuid.equals(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            int rawSlot = entry.getKey();
            if (rawSlot < 0 || rawSlot >= PickpocketInventoryBlueprint.SIZE) {
                continue;
            }
            if (!isValidMarker(entry.getValue())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (!DefaultPickpocketInventoryManager.isDefaultPickpocketInventory(topInventory)) {
            return;
        }
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        UUID editorUuid = DefaultPickpocketInventoryManager.getEditorUuid(topInventory);
        if (editorUuid == null || !editorUuid.equals(player.getUniqueId())) {
            return;
        }
        ItemStack[] raw = topInventory.getContents();
        ItemStack[] normalized = new ItemStack[PickpocketInventoryBlueprint.SIZE];
        for (int i = 0; i < PickpocketInventoryBlueprint.SIZE; i++) {
            ItemStack stack = i < raw.length ? raw[i] : null;
            normalized[i] = PickpocketInventoryBlueprint.normalizeSlot(stack);
            if (!isValidMarker(normalized[i])) {
                normalized[i] = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            }
        }
        String invalid = PickpocketInventoryBlueprint.validate(
                normalized,
                PickpocketInventoryBlueprint.MIN_USABLE_STEALABLE_SLOTS);
        if (invalid != null) {
            player.sendMessage(ChatColor.RED + "Default layout is invalid: " + invalid);
            Bukkit.getScheduler().runTask(PickpocketPlugin.getInstance(),
                    () -> DefaultPickpocketInventoryManager.openFor(player));
            return;
        }
        Config.setDefaultPickpocketInventoryLayout(normalized);
        Config.save();
        player.sendMessage(ChatColor.GREEN + "Saved global pickpocket default layout.");
    }

    private static boolean isValidMarker(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return true;
        }
        return PickpocketInventoryBlueprint.isMarkerMaterial(stack.getType());
    }

    private static boolean shouldCycleMarkerOnClick(
            InventoryClickEvent event,
            ItemStack cursor,
            ItemStack current) {
        if (cursor != null && cursor.getType() != Material.AIR) {
            return false;
        }
        if (current == null || !PickpocketInventoryBlueprint.isMarkerMaterial(current.getType())) {
            return false;
        }
        InventoryAction action = event.getAction();
        return action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME;
    }
}
