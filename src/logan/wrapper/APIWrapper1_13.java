package logan.wrapper;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class APIWrapper1_13 implements APIWrapper {

    @Override
    public Material getMaterialWhiteStainedGlassPane() {
        return Material.WHITE_STAINED_GLASS_PANE;
    }

    @Override
    public Sound getSoundBlockSnowStep() {
        return Sound.BLOCK_SNOW_STEP;
    }

    @Override
    public Sound getSoundEntityExperienceOrbPickup() {
        return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    }

    @Override
    public Sound getSoundBlockNoteBlockBass() {
        return Sound.BLOCK_NOTE_BLOCK_BASS;
    }

    @Override
    public Sound getSoundEntityItemPickup() {
        return Sound.ENTITY_ITEM_PICKUP;
    }

    @Override
    public ItemStack[] getInventoryStorageContents(Inventory inventory) {
        return inventory.getStorageContents();
    }
}
