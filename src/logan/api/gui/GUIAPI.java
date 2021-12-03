package logan.api.gui;

import logan.api.gui.util.PlaceholderManager;
import logan.api.listener.InventoryClickListener;
import logan.api.listener.InventoryCloseListener;
import logan.api.listener.InventoryListeners;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tre Logan
 */
public class GUIAPI {

    private static Map<Integer, Menu> registeredMenus = new ConcurrentHashMap<>();
    private static final @NotNull List<InventoryClickListener> inventoryClickListeners = new ArrayList<>();
    private static final @NotNull List<InventoryCloseListener> inventoryCloseListeners = new ArrayList<>();
    private static PlaceholderManager placeholderManager = new PlaceholderManager();

    public static void registerMenu(int id, Menu menu) {
        if (registeredMenus.containsKey(id)) return;
        registeredMenus.put(id, menu);
    }

    public static void registerInventoryClickListener(@NotNull InventoryClickListener listener) {
        inventoryClickListeners.add(listener);
    }

    public static void registerInventoryCloseListener(@NotNull InventoryCloseListener listener) {
        inventoryCloseListeners.add(listener);
    }

    public static void registerListeners(@NotNull JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new InventoryListeners(), plugin);
    }

    public static PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public static void callInventoryClickListeners(InventoryClickEvent event) {
        // Call all menu related inventory click events before calling listeners by other plugins.
        for (int key : registeredMenus.keySet())
            registeredMenus.get(key).onInventoryClick(event);

        // Call general inventory click listeners that can be unrelated to menus.
        for (InventoryClickListener listener : inventoryClickListeners)
            listener.onInventoryClick(event);
    }

    public static void callInventoryCloseListeners(InventoryCloseEvent event) {
        // Call all menu related inventory close listeners before processing other registered events.
        if (registeredMenus.isEmpty())
            return;
        Iterator<Integer> menuIterator = registeredMenus.keySet().iterator();
        while (menuIterator.hasNext()) {
            Menu registeredMenu = registeredMenus.get(menuIterator.next());
            Inventory inventory = event.getInventory();
            HumanEntity viewer = inventory.getViewers().get(0);

            if (registeredMenu.getViewer().equals(viewer)) {
                registeredMenu.setClosed(true);
                break;
            }
        }

        List<Integer> menusToClose = new ArrayList<>();

        registeredMenus.forEach((k, v) -> {
            if (registeredMenus.get(k).isClosed()) menusToClose.add(k);
        });
        menusToClose.forEach(menuId -> {
            registeredMenus.remove(menuId);
        });

        // Process other inventory close listeners now.
        for (InventoryCloseListener listener : inventoryCloseListeners)
            listener.onInventoryClose(event);
    }
}
