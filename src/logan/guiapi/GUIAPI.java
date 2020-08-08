package logan.guiapi;

import logan.guiapi.util.PlaceholderManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tre Logan
 */
public class GUIAPI
{

    private static Map<Integer, Menu> registeredMenus    = new ConcurrentHashMap<>();
    private static PlaceholderManager placeholderManager = new PlaceholderManager();

    public static void registerMenu(int id, Menu menu)
    {
        if (registeredMenus.containsKey(id)) return;
        registeredMenus.put(id, menu);
    }

    public static PlaceholderManager getPlaceholderManager()
    {
        return placeholderManager;
    }

    public static void callInventoryClickEvents(InventoryClickEvent event)
    {
        for (int key : registeredMenus.keySet())
        {
            registeredMenus.get(key).onInventoryClick(event);
        }
    }

    public static void callInventoryCloseEvents(InventoryCloseEvent event)
    {
        if (registeredMenus.isEmpty())
        {
            return;
        }
        Iterator<Integer> menuIterator = registeredMenus.keySet().iterator();
        while (menuIterator.hasNext())
        {
            Menu        registeredMenu = registeredMenus.get(menuIterator.next());
            Inventory   inventory      = event.getInventory();
            HumanEntity viewer         = inventory.getViewers().get(0);

            if (registeredMenu.getViewer().equals(viewer))
            {
                registeredMenu.setCloseCalledFromEvent(true);
                registeredMenu.setClosed(true);
                break;
            }
        }

        List<Integer> menusToClose = new ArrayList<>();

        registeredMenus.forEach((k, v) ->
        {
            if (registeredMenus.get(k).isClosed()) menusToClose.add(k);
        });
        menusToClose.forEach(menuId ->
        {
            registeredMenus.remove(menuId);
        });
    }
}
