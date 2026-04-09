package logan.pickpocket.skills;

import logan.pickpocket.config.Config;

public class SpeedSkill extends PlayerSkill {

    public static final float BASE_DELAY_SECONDS = 5.0f;

    SpeedSkill() {
        super(Skill.SPEED);
    }

    /**
     * Calculates the delay in seconds before the rummage inventory opens.
     * The delay is linearly calculated based on the skill level.
     * Max level is 100.
     *
     * @param level The player's Speed skill level (between 0 and MAX_LEVEL).
     * @return The delay in seconds.
     */
    public float getDelaySeconds(int level) {
        if (level < 0) level = 0;
        if (level > MAX_LEVEL) level = MAX_LEVEL;

        // E.g., if min delay configured is 1.0f at level 100
        float minDelay = Config.getMinSpeedSkillDelay();
        
        // Linear interpolation
        // Level 0 -> BASE_DELAY_SECONDS
        // Level MAX_LEVEL -> minDelay
        float progress = (float) level / MAX_LEVEL;
        return BASE_DELAY_SECONDS - (progress * (BASE_DELAY_SECONDS - minDelay));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
