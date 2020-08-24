package logan.wrapper;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface APIWrapper {

    Material getMaterialWhiteStainedGlassPane();

    Sound getSoundBlockSnowStep();

    Sound getSoundEntityExperienceOrbPickup();

    Sound getSoundBlockNoteBlockBass();

    Sound getSoundEntityItemPickup();

    ItemStack[] getInventoryStorageContents(Inventory inventory);
}
