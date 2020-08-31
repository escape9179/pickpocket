package logan.guiapi.fill;

import logan.guiapi.Menu;
import logan.guiapi.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class FillPlacer {

    private ItemStack itemStack;

    public FillPlacer(ItemStack fillItem) {
        setFill(fillItem);
    }

    public FillPlacer(Material material) {
        setFill(material);
    }

    public void setFill(ItemStack fillItem) {
        itemStack = fillItem;
    }

    public void setFill(Material material) {
        itemStack = new ItemStack(material);
    }

    public void placeIntermittently(Menu menu, int start, int spacing, Collection<Integer> slots, FillMode mode) {

        int size = menu.getSize();
        double times = ((double) (size - start) / spacing);
        times = Math.ceil(times);

        int position = start;
        for (int i = 0; i < times; i++) {

            switch (mode) {
                case PERMIT:
                    if (slots.contains(position)) fillSlot(menu, position);
                    break;
                case AVOID:
                    if (!slots.contains(position)) fillSlot(menu, position);
                    break;
                case IGNORE:
                default:
                    fillSlot(menu, position);
            }

            position += spacing;
        }
    }

    private void fillSlot(Menu menu, int position) {
        MenuItem fillItem = new MenuItem("", itemStack);
        menu.addItem(position, fillItem);
    }

    public void placeIntermittently(Menu menu, int start, int spacing) {
        this.placeIntermittently(menu, start, spacing, Collections.emptyList(), FillMode.IGNORE);
    }

    public enum FillMode {
        AVOID, PERMIT, IGNORE
    }
}
