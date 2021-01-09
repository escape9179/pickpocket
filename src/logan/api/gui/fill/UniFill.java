package logan.api.gui.fill;

import logan.api.gui.Menu;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Tre Logan
 */
public class UniFill implements Filler {

    private Material fillMaterial;

    public UniFill(Material fillMaterial) {
        this.fillMaterial = fillMaterial;
    }

    @Override
    public void fill(Menu menu) {
        this.fill(menu, Collections.emptyList(), FillPlacer.FillMode.IGNORE);
    }

    @Override
    public void fill(Menu menu, Collection<Integer> slots, FillPlacer.FillMode mode) {
        FillPlacer fillPlacer = new FillPlacer(fillMaterial);
        fillPlacer.placeIntermittently(menu, 0, 1, slots, mode);
    }
}
