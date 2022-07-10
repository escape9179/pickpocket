package logan.api.gui;

import logan.api.gui.fill.FillPlacer;
import logan.api.gui.fill.Filler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Map;

public interface InventoryMenu {
    int getId();

    void show(Player player);

    void close();

    void update();

    void clear();

    void setClosed(boolean value);

    boolean isClosed();

    void setRows(int rows);

    void fill(Filler fillPattern);

    void fill(Filler fillPattern, Collection<Integer> slots, FillPlacer.FillMode mode);

    MenuItem addItem(int slot, MenuItem menuItem);

    void removeItem(int slot, MenuItem menuItem);

    Map<Integer, MenuItem> getMenuItems();

    String getTitle();

    Inventory getInventory();

    int getSize();

    int getTopLeft();

    int getTopRight();

    int getBottomLeft();

    int getBottomRight();

    Player getViewer();

    void onInventoryClick(InventoryClickEvent event);
}
