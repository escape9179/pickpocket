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

    public PickpocketUser getThief() {
        return thief;
    }

    public PickpocketUser getVictim() {
        return victim;
    }

    public boolean isRummaging() {
        return rummaging;
    }

    public void setRummaging(boolean rummaging) {
        this.rummaging = rummaging;
    }

    public RummageInventory getRummageInventory() {
        return rummageInventory;
    }

    public void setRummageInventory(RummageInventory openRummageInventory) {
        this.rummageInventory = openRummageInventory;
    }

    public RummageSessionState getRummageState() {
        return rummageState;
    }

    public boolean isThief(PickpocketUser user) {
        return user != null && thief.getUuid().equals(user.getUuid());
    }

    public boolean isVictim(PickpocketUser user) {
        return user != null && victim.getUuid().equals(user.getUuid());
    }

    void clearEphemeralState() {
        rummaging = false;
        rummageInventory = null;
        rummageState.reset();
    }
}
