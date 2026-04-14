package logan.pickpocket.skills;

/**
 * Skill that controls how many item slots are revealed in each active rummage row.
 */
public final class RevealSkill extends PlayerSkill {

    private static final int MAX_REVEALED_SLOTS_PER_ROW = 8;
    private static final int MIN_REVEALED_SLOTS_PER_ROW = 1;

    RevealSkill() {
        super(Skill.REVEAL);
    }

    /**
     * @return number of victim slots to reveal for each active menu row, scaled 1:1 with skill level and capped at 8
     */
    public int getRevealedSlotsPerMenuRow() {
        int level = Math.max(MIN_REVEALED_SLOTS_PER_ROW, getLevel());
        return Math.min(MAX_REVEALED_SLOTS_PER_ROW, level);
    }
}
