package logan.api.gui.fill;

import logan.api.gui.PlayerInventoryMenu;

import java.util.Collection;

public interface Filler {

    void fill(PlayerInventoryMenu menu);

    void fill(PlayerInventoryMenu menu, Collection<Integer> slots, FillPlacer.FillMode mode);
}
