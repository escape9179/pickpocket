package logan.pickpocket.managers;

import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private static final Map<UUID, PickpocketUser> users = new ConcurrentHashMap<>();

    private UserManager() {
    }

    public static Map<UUID, PickpocketUser> getUsers() {
        return users;
    }

    public static void addUser(UUID uuid, PickpocketUser user) {
        users.put(uuid, user);
    }
    
    public static void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    public static PickpocketUser get(Player player) {
        return users.computeIfAbsent(player.getUniqueId(), PickpocketUser::new);
    }

}
