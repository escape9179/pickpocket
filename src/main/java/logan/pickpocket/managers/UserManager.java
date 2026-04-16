package logan.pickpocket.managers;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache of {@link PickpocketUser} instances by player UUID.
 */
public class UserManager {

    private static final Map<UUID, PickpocketUser> users = new ConcurrentHashMap<>();

    private UserManager() {
    }

    /**
     * @return mutable user cache
     */
    public static Map<UUID, PickpocketUser> getUsers() {
        return users;
    }

    /**
     * Adds or replaces a cached user.
     *
     * @param uuid player UUID
     * @param user user object
     */
    public static void addUser(UUID uuid, PickpocketUser user) {
        users.put(uuid, user);
    }
    
    /**
     * Removes a cached user.
     *
     * @param uuid player UUID
     */
    public static void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    /**
     * Resolves or creates a cached user for a player.
     *
     * @param player Bukkit player
     * @return cached user object
     */
    public static PickpocketUser get(Player player) {
        return users.computeIfAbsent(player.getUniqueId(), PickpocketUser::new);
    }

}
