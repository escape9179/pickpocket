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
        rummageInventory = null;
        rummageState.reset();
    }
}
