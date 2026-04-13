package logan.pickpocket.skills;

/**
 * Skill that increases how many item slots can be revealed per menu row in rummage.
 */
public final class RevealSkill extends PlayerSkill {

    private static final int LEVELS_PER_REVEAL_BONUS = 20;

    RevealSkill() {
        super(Skill.REVEAL);
    }

    /**
     * @return additional revealed slots per menu row (on top of the baseline of one)
     */
    public int getRevealLevelBonus() {
        return Math.max(0, getLevel() / LEVELS_PER_REVEAL_BONUS);
    }

    /**
     * @return target number of revealed victim slots for each full menu row (baseline one, plus skill)
     */
    public int getRevealedSlotsPerMenuRow() {
        return 1 + getRevealLevelBonus();
    }
}
