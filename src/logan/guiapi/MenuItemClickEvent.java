package logan.guiapi;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Tre Logan
 */
public class MenuItemClickEvent {
    
    private Player player;
    private MenuItem menuItem;
    private InventoryClickEvent clickEvent;
    
    public MenuItemClickEvent(InventoryClickEvent event) {
        this.player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) {
            event.setCancelled(true);
            return;
        }

        this.menuItem = new MenuItem(event.getCurrentItem());
        clickEvent = event;
    }
    
    public MenuItem getMenuItem() {
        return menuItem;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public InventoryClickEvent getInventoryClickEvent() {
        return clickEvent;
    }
    
    public void cancel(boolean cancel) {
        clickEvent.setCancelled(cancel);
    }
    
}
