package logan.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helpers for building custom-textured {@link Material#PLAYER_HEAD} items.
 * Textures are applied via {@link PlayerProfile} / {@link PlayerTextures},
 * so no {@code GameProfile} reflection is required on modern Spigot.
 */
public final class HeadUtils {

    private static final Logger LOGGER = Logger.getLogger(HeadUtils.class.getName());

    /**
     * Oak-log "numbered block" heads from <a href="https://freshcoal.com/maincollection.php">freshcoal.com</a>,
     * indexed by digit 0-9.
     */
    private static final String[] NUMBER_TEXTURE_URLS = {
            "http://textures.minecraft.net/texture/0ebe7e5215169a699acc6cefa7b73fdb108db87bb6dae2849fbe24714b27",
            "http://textures.minecraft.net/texture/71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530",
            "http://textures.minecraft.net/texture/4cd9eeee883468881d83848a46bf3012485c23f75753b8fbe8487341419847",
            "http://textures.minecraft.net/texture/1d4eae13933860a6df5e8e955693b95a8c3b15c36b8b587532ac0996bc37e5",
            "http://textures.minecraft.net/texture/d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5",
            "http://textures.minecraft.net/texture/6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2",
            "http://textures.minecraft.net/texture/334b36de7d679b8bbc725499adaef24dc518f5ae23e716981e1dcc6b2720ab",
            "http://textures.minecraft.net/texture/6db6eb25d1faabe30cf444dc633b5832475e38096b7e2402a3ec476dd7b9",
            "http://textures.minecraft.net/texture/59194973a3f17bda9978ed6273383997222774b454386c8319c04f1f4f74c2b5",
            "http://textures.minecraft.net/texture/e67caf7591b38e125a8017d58cfc6433bfaf84cd499d794f41d10bff2e5b840",
    };

    private static final ItemStack[] NUMBER_HEAD_CACHE = new ItemStack[NUMBER_TEXTURE_URLS.length];

    private HeadUtils() {
    }

    /**
     * Builds a new {@link Material#PLAYER_HEAD} item with the supplied skin texture.
     * Falls back to a plain untextured head if the URL cannot be parsed or applied.
     *
     * @param textureUrl full {@code textures.minecraft.net} skin URL
     * @return textured player-head item stack
     */
    public static ItemStack createTexturedHead(String textureUrl) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (textureUrl == null || textureUrl.isEmpty()) {
            return head;
        }
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) {
            return head;
        }
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(textureUrl));
        } catch (MalformedURLException exception) {
            LOGGER.log(Level.WARNING, "Invalid head texture URL: " + textureUrl, exception);
            return head;
        }
        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Returns a cached numbered "oak log" head for the given digit 0-9.
     * The returned stack is a clone that may be freely mutated by callers.
     *
     * @param digit 0-9 inclusive
     * @return a cloned textured head item stack
     * @throws IllegalArgumentException when {@code digit} is out of range
     */
    public static ItemStack numberHead(int digit) {
        if (digit < 0 || digit >= NUMBER_TEXTURE_URLS.length) {
            throw new IllegalArgumentException("digit must be 0-9, got " + digit);
        }
        ItemStack cached = NUMBER_HEAD_CACHE[digit];
        if (cached == null) {
            cached = createTexturedHead(NUMBER_TEXTURE_URLS[digit]);
            NUMBER_HEAD_CACHE[digit] = cached;
        }
        return cached.clone();
    }
}
