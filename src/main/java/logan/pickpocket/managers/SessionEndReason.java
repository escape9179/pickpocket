package logan.pickpocket.managers;

/**
 * Why a pickpocket session ended; recorded in {@link PickpocketSessionManager} history.
 */
public enum SessionEndReason {
    UNLINKED,
    TASK_CANCELLED,
    PREDATOR_MOVED,
    VICTIM_MOVED,
    PICKPOCKET_INVENTORY_CLOSED,
    VICTIM_INVENTORY_OPENED,
    CHANCES_DEPLETED,
    NO_CLICKABLE_SLOTS_REMAINING
}
