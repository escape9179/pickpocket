package logan.pickpocket.skills;

/**
 * Skill that reduces how many revealed slots are forgotten each stage.
 */
public final class MemorySkill extends PlayerSkill {

    private static final int LEVELS_PER_FORGET_REDUCTION = 25;

    MemorySkill() {
        super(Skill.MEMORY);
    }

    /**
     * Rows after the initial row are represented as stage indexes (row2 = stage 1).
     */
    /**
     * Computes number of previously revealed slots to forget for a stage.
     *
     * @param stageIndex rummage stage index (row 2 = stage 1)
     * @return number of slots to forget
     */
    public int getForgetCount(int stageIndex) {
        int clampedStage = Math.max(0, stageIndex);
        int reduction = Math.max(0, getLevel() / LEVELS_PER_FORGET_REDUCTION);
        return Math.max(0, clampedStage - reduction);
    }
}
