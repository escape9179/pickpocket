package logan.pickpocket.skills;

/**
 * Skill that reduces the chance revealed slots are forgotten on rummage expansion.
 */
public final class MemorySkill extends PlayerSkill {

    private static final int MAX_MEMORY_LEVEL = 100;

    MemorySkill() {
        super(Skill.MEMORY);
    }

    /**
     * Computes linear per-slot forgetting chance for previous rows on expansion.
     *
     * @return chance in [0.0, 1.0], where level 0 => 1.0 and level 100 => 0.0
     */
    public double getForgetChance() {
        double clampedLevel = Math.max(0, Math.min(MAX_MEMORY_LEVEL, getLevel()));
        return 1.0D - (clampedLevel / MAX_MEMORY_LEVEL);
    }
}
