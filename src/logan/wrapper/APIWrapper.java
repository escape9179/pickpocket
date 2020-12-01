package logan.wrapper;

import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface APIWrapper {

    Material getMaterialWhiteStainedGlassPane();

    ItemStack getItemStackRedStainedGlassPane();

    Sound getSoundBlockSnowStep();

    Sound getSoundEntityExperienceOrbPickup();

    Sound getSoundBlockNoteBlockBass();

    Sound getSoundEntityItemPickup();

    ItemStack[] getInventoryStorageContents(Inventory inventory);

    FlagRegistry getWorldGuardFlagRegistiry();
}
