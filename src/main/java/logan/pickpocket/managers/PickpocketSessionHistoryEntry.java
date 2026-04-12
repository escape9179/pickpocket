package logan.pickpocket.managers;

import java.util.UUID;

/**
 * Immutable record of a completed pickpocket session.
 */
public final class PickpocketSessionHistoryEntry {

    private final UUID thiefId;
    private final UUID victimId;
    private final long endedAtEpochMilli;
    private final SessionEndReason reason;

    public PickpocketSessionHistoryEntry(UUID thiefId, UUID victimId, long endedAtEpochMilli,
            SessionEndReason reason) {
        this.thiefId = thiefId;
        this.victimId = victimId;
        this.endedAtEpochMilli = endedAtEpochMilli;
        this.reason = reason;
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
}
