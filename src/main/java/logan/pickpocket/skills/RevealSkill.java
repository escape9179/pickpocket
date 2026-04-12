package logan.pickpocket.skills;

/**
 * Skill that increases how many item slots can be revealed per stage.
 */
public final class RevealSkill extends PlayerSkill {

    private static final int LEVELS_PER_REVEAL_BONUS = 20;

    RevealSkill() {
        super(Skill.REVEAL);
    }

    /**
     * @return additional revealed slots contributed by reveal level
     */
    public int getRevealLevelBonus() {
        return Math.max(0, getLevel() / LEVELS_PER_REVEAL_BONUS);
    }
}
