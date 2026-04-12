package logan.pickpocket.managers;

/**
 * Why a pickpocket session ended; recorded in {@link PickpocketSessionManager} history.
 */
public enum SessionEndReason {
    UNLINKED,
    TASK_CANCELLED,
    PREDATOR_MOVED,
    VICTIM_MOVED,
    RUMMAGE_INVENTORY_CLOSED
}
