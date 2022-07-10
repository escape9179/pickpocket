package logan.api.gui;

import logan.api.gui.fill.FillPlacer;
import logan.api.gui.fill.Filler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tre Logan
 */
public class PlayerInventoryMenu implements InventoryMenu {

    private static int nextId = 0;

    private final int id;
    private String title;
    private Inventory inventory;
    private int size;
    private boolean closed;

    private Player viewer = null;

    private Map<Integer, MenuItem> menuItems = new ConcurrentHashMap<>();

    public PlayerInventoryMenu(String title, int rows) {
        id = nextId;
        this.title = title;
        size = rows * 9;
        nextId++;
        inventory = Bukkit.createInventory(null, size, title);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void show(Player player) {
        /* Create inventory and add items */
        menuItems.forEach((s, mi) -> inventory.setItem(s, mi.getItemStack()));
        viewer = player;

        GUIAPI.registerMenu(id, this);

        player.openInventory(inventory);
    }

    @Override
    public void close() {
        viewer.closeInventory();
        viewer = null;
        closed = true;
    }

    @Override
    public void update() {
        menuItems.forEach((s, mi) -> inventory.setItem(s, mi.getItemStack()));
    }

    @Override
    public void clear() {
        menuItems.clear();
    }

    @Override
    public void setClosed(boolean value) {
        closed = value;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setRows(int rows) {
        this.size = rows * 9;
    }

    @Override
    public void fill(Filler fillPattern) {
        this.fill(fillPattern, Collections.emptyList(), FillPlacer.FillMode.IGNORE);
    }

    @Override
    public void fill(Filler fillPattern, Collection<Integer> slots, FillPlacer.FillMode mode) {
        fillPattern.fill(this, slots, mode);
    }

    @Override
    public MenuItem addItem(int slot, MenuItem menuItem) {
        return menuItems.put(slot, menuItem);
    }

    @Override
    public void removeItem(int slot, MenuItem menuItem) {
        menuItems.remove(slot);
    }

    @Override
    public Map<Integer, MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getTopLeft() {
        return 0;
    }

    @Override
    public int getTopRight() {
        return 8;
    }

    @Override
    public int getBottomLeft() {
        return size - 9;
    }

    @Override
    public int getBottomRight() {
        return size - 1;
    }

    @Override
    public Player getViewer() {
        return viewer;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(viewer.getUniqueId()).equals(event.getWhoClicked().getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        menuItems.keySet().stream()
                .filter(s -> s == event.getSlot())
                .forEach(s -> menuItems.get(s).onClick(new MenuItemClickEvent(event)));
    }
}
