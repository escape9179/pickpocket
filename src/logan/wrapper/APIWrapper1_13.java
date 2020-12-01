package logan.wrapper;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class APIWrapper1_13 implements APIWrapper {

    @Override
    public Material getMaterialWhiteStainedGlassPane() {
        return Material.WHITE_STAINED_GLASS_PANE;
    }

    @Override
    public ItemStack getItemStackRedStainedGlassPane() {
        return new ItemStack(Material.RED_STAINED_GLASS_PANE);
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

    @Override
    public FlagRegistry getWorldGuardFlagRegistiry() {
        try {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            WorldGuardPlugin worldGuardPlugin
                    = (WorldGuardPlugin) worldGuardClass
                    .getMethod("getInstance")
                    .invoke(null);
            return (FlagRegistry) worldGuardPlugin.getClass()
                    .getMethod("getFlagRegistry")
                    .invoke(worldGuardPlugin);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
