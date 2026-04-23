package logan.pickpocket.skills;

/**
 * Skill that reduces the delay before a revealed item transfer completes.
 */
public final class QuicknessSkill extends PlayerSkill {

    public static final float MAX_TRANSFER_DELAY_SECONDS = 3.0f;
    public static final float MIN_TRANSFER_DELAY_SECONDS = 1.0f;
    private static final int MAX_QUICKNESS_LEVEL = 10;

    QuicknessSkill() {
        super(Skill.QUICKNESS);
    }

    @Override
    public int getMaxLevel() {
        return MAX_QUICKNESS_LEVEL;
    }

    /**
     * Computes linear transfer delay based on current quickness level.
     *
     * @return transfer delay in seconds, clamped to [1.0, 3.0]
     */
    public float getTransferDelaySeconds() {
        int clampedLevel = Math.max(0, Math.min(getMaxLevel(), getLevel()));
        float progress = (float) clampedLevel / getMaxLevel();
        return MAX_TRANSFER_DELAY_SECONDS
                - (progress * (MAX_TRANSFER_DELAY_SECONDS - MIN_TRANSFER_DELAY_SECONDS));
    }
}
