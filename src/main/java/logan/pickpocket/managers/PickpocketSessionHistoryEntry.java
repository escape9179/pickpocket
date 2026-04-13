package logan.pickpocket.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Immutable record of a completed pickpocket session.
 */
@SerializableAs("PickpocketSessionHistoryEntry")
public final class PickpocketSessionHistoryEntry implements ConfigurationSerializable {

    private static final String KEY_THIEF = "thiefId";
    private static final String KEY_VICTIM = "victimId";
    private static final String KEY_ENDED_AT = "endedAtEpochMilli";
    private static final String KEY_REASON = "reason";

    private final UUID thiefId;
    private final UUID victimId;
    private final long endedAtEpochMilli;
    private final SessionEndReason reason;

    public PickpocketSessionHistoryEntry(UUID thiefId, UUID victimId, long endedAtEpochMilli,
            SessionEndReason reason) {
        this.thiefId = Objects.requireNonNull(thiefId, "thiefId");
        this.victimId = Objects.requireNonNull(victimId, "victimId");
        this.endedAtEpochMilli = endedAtEpochMilli;
        this.reason = Objects.requireNonNull(reason, "reason");
    }

    public UUID getThiefId() {
        return thiefId;
    }

    public UUID getVictimId() {
        return victimId;
    }

    public long getEndedAtEpochMilli() {
        return endedAtEpochMilli;
    }

    public SessionEndReason getReason() {
        return reason;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_THIEF, thiefId.toString());
        map.put(KEY_VICTIM, victimId.toString());
        map.put(KEY_ENDED_AT, endedAtEpochMilli);
        map.put(KEY_REASON, reason.name());
        return map;
    }

    /**
     * Deserializes from Bukkit configuration map.
     *
     * @param map serialized map
     * @return entry instance
     */
    public static PickpocketSessionHistoryEntry deserialize(Map<String, Object> map) {
        UUID thief = parseUuid(map.get(KEY_THIEF));
        UUID victim = parseUuid(map.get(KEY_VICTIM));
        long endedAt = toLong(map.get(KEY_ENDED_AT));
        SessionEndReason r = parseReason(map.get(KEY_REASON));
        return new PickpocketSessionHistoryEntry(thief, victim, endedAt, r);
    }

    private static UUID parseUuid(Object value) {
        if (value instanceof UUID) {
            return (UUID) value;
        }
        return UUID.fromString(String.valueOf(value));
    }

    private static long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private static SessionEndReason parseReason(Object value) {
        if (value instanceof SessionEndReason) {
            return (SessionEndReason) value;
        }
        try {
            return SessionEndReason.valueOf(String.valueOf(value));
        } catch (IllegalArgumentException ex) {
            return SessionEndReason.UNLINKED;
        }
    }
}
