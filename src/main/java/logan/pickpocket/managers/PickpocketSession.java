package logan.pickpocket.managers;

import logan.pickpocket.user.PickpocketUser;
import logan.pickpocket.inventory.RummageInventory;

/**
 * Active pickpocket attempt between a thief and a victim (delay + rummage UI).
 */
public final class PickpocketSession {

    private final PickpocketUser thief;
    private final PickpocketUser victim;

    private boolean rummaging;
    private long rummageStartEpochMilli = -1L;
    private RummageInventory rummageInventory;
    private final RummageSessionState rummageState = new RummageSessionState();

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
    public boolean isRummaging() {
        return rummaging;
    }

    /**
     * Sets whether rummage UI is considered active.
     *
     * @param rummaging active rummage state
     */
    public void setRummaging(boolean rummaging) {
        this.rummaging = rummaging;
    }

    /**
     * Marks the beginning of active rummaging for elapsed-time tracking.
     *
     * @param startEpochMilli start time in epoch milliseconds
     */
    public void setRummageStartEpochMilli(long startEpochMilli) {
        this.rummageStartEpochMilli = startEpochMilli;
    }

    /**
     * Consumes and resets the tracked rummage elapsed time.
     *
     * @param endEpochMilli end time in epoch milliseconds
     * @return elapsed rummage time in milliseconds
     */
    public long consumeRummageElapsedMillis(long endEpochMilli) {
        if (rummageStartEpochMilli <= 0L || endEpochMilli <= rummageStartEpochMilli) {
            rummageStartEpochMilli = -1L;
            return 0L;
        }
        long elapsed = endEpochMilli - rummageStartEpochMilli;
        rummageStartEpochMilli = -1L;
        return elapsed;
    }

    /**
     * @return active rummage inventory instance, if present
     */
    public RummageInventory getRummageInventory() {
        return rummageInventory;
    }

    /**
     * Sets active rummage inventory reference.
     *
     * @param openRummageInventory inventory instance
     */
    public void setRummageInventory(RummageInventory openRummageInventory) {
        this.rummageInventory = openRummageInventory;
    }

    /**
     * @return mutable rummage state for this session
     */
    public RummageSessionState getRummageState() {
        return rummageState;
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
        rummaging = false;
        rummageStartEpochMilli = -1L;
        rummageInventory = null;
        rummageState.reset();
    }
}
