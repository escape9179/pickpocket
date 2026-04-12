package logan.pickpocket.skills;

public final class MemorySkill extends PlayerSkill {

    private static final int LEVELS_PER_FORGET_REDUCTION = 25;

    MemorySkill() {
        super(Skill.MEMORY);
    }

    /**
     * Rows after the initial row are represented as stage indexes (row2 = stage 1).
     */
    public int getForgetCount(int stageIndex) {
        int clampedStage = Math.max(0, stageIndex);
        int reduction = Math.max(0, getLevel() / LEVELS_PER_FORGET_REDUCTION);
        return Math.max(0, clampedStage - reduction);
    }
}
