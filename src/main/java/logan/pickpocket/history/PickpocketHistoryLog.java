package logan.pickpocket.history;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.managers.PickpocketSessionHistoryEntry;

/**
 * Persists completed pickpocket sessions per player under {@code history/<uuid>.yml} and
 * aggregates most-frequent thief/victim on demand.
 */
public final class PickpocketHistoryLog {

    private static final String KEY_SESSIONS = "sessions";
    private static final ConcurrentHashMap<UUID, Object> FILE_LOCKS = new ConcurrentHashMap<>();

    private PickpocketHistoryLog() {
    }

    /**
     * Appends one session record to both participants' history files.
     *
     * @param entry completed session
     */
    public static void append(PickpocketSessionHistoryEntry entry) {
        if (entry == null) {
            return;
        }
        appendToPlayerFile(entry.getThiefId(), entry);
        appendToPlayerFile(entry.getVictimId(), entry);
    }

    /**
     * @param player thief perspective
     * @return victim UUID this player successfully pickpocketed in the most sessions, if any
     */
    public static Optional<UUID> mostStealsFrom(UUID player) {
        return topCounterpartUuid(player, true);
    }

    /**
     * @param player victim perspective
     * @return thief UUID who pickpocketed this player in the most sessions, if any
     */
    public static Optional<UUID> mostStealsBy(UUID player) {
        return topCounterpartUuid(player, false);
    }

    private static Optional<UUID> topCounterpartUuid(UUID player, boolean asThief) {
        List<PickpocketSessionHistoryEntry> sessions = readSessions(player);
        Map<UUID, Integer> counts = new HashMap<>();
        for (PickpocketSessionHistoryEntry e : sessions) {
            if (asThief) {
                if (player.equals(e.getThiefId())) {
                    UUID other = e.getVictimId();
                    counts.merge(other, 1, Integer::sum);
                }
            } else {
                if (player.equals(e.getVictimId())) {
                    UUID other = e.getThiefId();
                    counts.merge(other, 1, Integer::sum);
                }
            }
        }
        return counts.entrySet().stream()
                .max(Comparator
                        .comparingInt(Map.Entry<UUID, Integer>::getValue)
                        .thenComparing(entry -> entry.getKey().toString(), Comparator.reverseOrder()))
                .map(Map.Entry::getKey);
    }

    private static void appendToPlayerFile(UUID playerId, PickpocketSessionHistoryEntry entry) {
        synchronized (lockFor(playerId)) {
            File file = historyFile(playerId);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            List<Object> sessions = new ArrayList<>();
            List<?> existing = yaml.getList(KEY_SESSIONS);
            if (existing != null) {
                sessions.addAll(existing);
            }
            sessions.add(entry);
            yaml.set(KEY_SESSIONS, sessions);
            try {
                yaml.save(file);
            } catch (IOException e) {
                PickpocketPlugin.log("Failed saving pickpocket history for " + playerId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static List<PickpocketSessionHistoryEntry> readSessions(UUID playerId) {
        synchronized (lockFor(playerId)) {
            File file = historyFile(playerId);
            if (!file.exists()) {
                return List.of();
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            List<?> raw = yaml.getList(KEY_SESSIONS);
            if (raw == null || raw.isEmpty()) {
                return List.of();
            }
            List<PickpocketSessionHistoryEntry> out = new ArrayList<>();
            for (Object o : raw) {
                Optional<PickpocketSessionHistoryEntry> parsed = deserializeEntry(o);
                parsed.ifPresent(out::add);
            }
            return out;
        }
    }

    @SuppressWarnings("unchecked")
    private static Optional<PickpocketSessionHistoryEntry> deserializeEntry(Object o) {
        // In-memory list elements: same JVM round-trip before YAML save may still be typed instances.
        if (o instanceof PickpocketSessionHistoryEntry) {
            return Optional.of((PickpocketSessionHistoryEntry) o);
        }
        // Typical path after loadConfiguration(): each session is a Map (e.g. LinkedHashMap) with field keys
        // and Bukkit's "==" type tag; try registered deserializer, then our field-based deserialize.
        if (o instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) o;
            try {
                Object rebuilt = ConfigurationSerialization.deserializeObject(map);
                if (rebuilt instanceof PickpocketSessionHistoryEntry) {
                    return Optional.of((PickpocketSessionHistoryEntry) rebuilt);
                }
            } catch (IllegalArgumentException ignored) {
                // fall through to manual parse
            }
            try {
                return Optional.of(PickpocketSessionHistoryEntry.deserialize(map));
            } catch (RuntimeException ignored) {
                return Optional.empty();
            }
        }
        // Nested config can appear as ConfigurationSection instead of a bare Map in some API paths.
        if (o instanceof ConfigurationSection) {
            Map<String, Object> map = sectionToMap((ConfigurationSection) o);
            try {
                return Optional.of(PickpocketSessionHistoryEntry.deserialize(map));
            } catch (RuntimeException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private static Map<String, Object> sectionToMap(ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
        for (String key : section.getKeys(false)) {
            map.put(key, section.get(key));
        }
        return map;
    }

    private static File historyFile(UUID playerId) {
        return new File(new File(PickpocketPlugin.getInstance().getDataFolder(), "history"),
                playerId + ".yml");
    }

    private static Object lockFor(UUID playerId) {
        return FILE_LOCKS.computeIfAbsent(playerId, u -> new Object());
    }
}
