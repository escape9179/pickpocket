package logan.pickpocket.managers;

import logan.pickpocket.user.PickpocketUser;
import logan.pickpocket.inventory.PickpocketInventory;

/**
 * Active pickpocket attempt between a thief and a victim (delay + rummage UI).
 */
public final class PickpocketSession {

    private final PickpocketUser thief;
    private final PickpocketUser victim;

    private boolean pickpocketing;
    private long pickpocketStartEpochMilli = -1L;
    private PickpocketInventory pickpocketInventory;
    private final PickpocketSessionState pickpocketState = new PickpocketSessionState();

    PickpocketSession(PickpocketUser thief, PickpocketUser victim) {
        this.thief = thief;
        this.victim = victim;
    }

    /**
     * @return thief in this session
     */
    public PickpocketUser getThief() {
        return thief;
    }

    /**
     * @return victim in this session
     */
    public PickpocketUser getVictim() {
        return victim;
    }

    /**
     * @return whether the thief currently has rummage UI open
     */
    public boolean isPickpocketing() {
        return pickpocketing;
    }

    /**
     * Sets whether rummage UI is considered active.
     *
     * @param rummaging active rummage state
     */
    public void setPickpocketing(boolean pickpocketing) {
        this.pickpocketing = pickpocketing;
    }

    /**
     * Marks the beginning of active rummaging for elapsed-time tracking.
     *
     * @param startEpochMilli start time in epoch milliseconds
     */
    public void setPickpocketStartEpochMilli(long startEpochMilli) {
        this.pickpocketStartEpochMilli = startEpochMilli;
    }

    /**
     * Consumes and resets the tracked rummage elapsed time.
     *
     * @param endEpochMilli end time in epoch milliseconds
     * @return elapsed rummage time in milliseconds
     */
    public long consumePickpocketElapsedMillis(long endEpochMilli) {
        if (pickpocketStartEpochMilli <= 0L || endEpochMilli <= pickpocketStartEpochMilli) {
            pickpocketStartEpochMilli = -1L;
            return 0L;
        }
        long elapsed = endEpochMilli - pickpocketStartEpochMilli;
        pickpocketStartEpochMilli = -1L;
        return elapsed;
    }

    /**
     * @return active rummage inventory instance, if present
     */
    public PickpocketInventory getPickpocketInventory() {
        return pickpocketInventory;
    }

    /**
     * Sets active rummage inventory reference.
     *
     * @param openPickpocketInventory inventory instance
     */
    public void setPickpocketInventory(PickpocketInventory openPickpocketInventory) {
        this.pickpocketInventory = openPickpocketInventory;
    }

    /**
     * @return mutable rummage state for this session
     */
    public PickpocketSessionState getPickpocketState() {
        return pickpocketState;
    }

    /**
     * @param user user to compare
     * @return true if user is the session thief
     */
    public boolean isThief(PickpocketUser user) {
        return user != null && thief.getUuid().equals(user.getUuid());
    }

    /**
     * @param user user to compare
     * @return true if user is the session victim
     */
    public boolean isVictim(PickpocketUser user) {
        return user != null && victim.getUuid().equals(user.getUuid());
    }

    /**
     * Clears non-persistent per-session runtime references.
     */
    void clearEphemeralState() {
        pickpocketing = false;
        pickpocketStartEpochMilli = -1L;
        pickpocketInventory = null;
        pickpocketState.reset();
    }
}
