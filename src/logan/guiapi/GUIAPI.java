package logan.guiapi;

import logan.guiapi.util.PlaceholderManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tre Logan
 */
public class GUIAPI {

    private static Map<Integer, Menu> registeredMenus = new ConcurrentHashMap<>();
    private static PlaceholderManager placeholderManager = new PlaceholderManager();

    public static void registerMenu(int id, Menu menu) {
        if (registeredMenus.containsKey(id)) return;
        registeredMenus.put(id, menu);
    }

    public static PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public static void callInventoryClickEvents(InventoryClickEvent event) {
        for (int key : registeredMenus.keySet()) {
            System.out.println("Calling event for menu " + key + ".");
            registeredMenus.get(key).onInventoryClick(event);
        }
    }

    public static void callInventoryCloseEvents(InventoryCloseEvent event) {
        if (registeredMenus.isEmpty()) return;
        Iterator<Integer> menuIterator = registeredMenus.keySet().iterator();
        while (menuIterator.hasNext()) {
            int id = menuIterator.next();
            Menu menu = registeredMenus.get(id);
            if (menu.shouldClose()) {
                menuIterator.remove();
                registeredMenus.remove(id);
            }
        }
    }
}
