package logan.guiapi.fill;

import logan.guiapi.Menu;

import java.util.Collection;

public interface Filler {

    void fill(Menu menu);

    void fill(Menu menu, Collection<Integer> slots, FillPlacer.FillMode mode);
}
