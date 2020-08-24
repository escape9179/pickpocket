package logan.wrapper;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class APIWrapper1_8 implements APIWrapper {

    @Override
    public Material getMaterialWhiteStainedGlassPane() {
        return Material.valueOf("STAINED_GLASS_PANE");
    }

    @Override
    public Sound getSoundBlockSnowStep() {
        return Sound.valueOf("STEP_SNOW");
    }

    @Override
    public Sound getSoundEntityExperienceOrbPickup() {
        return Sound.valueOf("ORB_PICKUP");
    }

    @Override
    public Sound getSoundBlockNoteBlockBass() {
        return Sound.valueOf("NOTE_BASS");
    }

    @Override
    public Sound getSoundEntityItemPickup() {
        return Sound.valueOf("ITEM_PICKUP");
    }

    @Override
    public ItemStack[] getInventoryStorageContents(Inventory inventory) {
        ItemStack[] inventoryContents = inventory.getContents();
        ItemStack[] storageContents = new ItemStack[35];
        for (int i = 0; i < storageContents.length; i++) {
            storageContents[i] = inventoryContents[i];
        }
        return storageContents;
    }
}
