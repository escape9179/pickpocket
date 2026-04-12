package logan.pickpocket.skills;

public final class RevealSkill extends PlayerSkill {

    private static final int LEVELS_PER_REVEAL_BONUS = 20;

    RevealSkill() {
        super(Skill.REVEAL);
    }

    public int getRevealLevelBonus() {
        return Math.max(0, getLevel() / LEVELS_PER_REVEAL_BONUS);
    }
}
