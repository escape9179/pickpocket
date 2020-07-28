package logan.guiapi.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderManager {

    private Map<String, Function<Player, String>> placeholderMap = new HashMap<>();

    public PlaceholderManager() {

        addPlaceholder("player_name", Player::getName);

        addPlaceholder("player_count", (player) -> {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            return String.valueOf(onlinePlayers.size());
        });

    }

    public void addPlaceholder(String placeholder, Function<Player, String> function) {
        placeholderMap.put("%" + placeholder + "%", function);
    }

    Map<String, Function<Player, String>> getPlaceholderMap() {
        return placeholderMap;
    }

}
