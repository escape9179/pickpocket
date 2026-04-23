package logan.pickpocket.skills;

import logan.pickpocket.user.PickpocketUser;

/**
 * Container for all skills associated with a single pickpocket user.
 */
public class SkillModule {

    private SpeedSkill speedSkill;
    private RevealSkill revealSkill;
    private MemorySkill memorySkill;
    private QuicknessSkill quicknessSkill;

    /**
     * Creates default skill instances for a user.
     *
     * @param user owning pickpocket user
     */
    public SkillModule(PickpocketUser user) {
        this.speedSkill = new SpeedSkill();
        this.revealSkill = new RevealSkill();
        this.memorySkill = new MemorySkill();
        this.quicknessSkill = new QuicknessSkill();
    }

    /**
     * @return speed skill state
     */
    public SpeedSkill getSpeedSkill() {
        return speedSkill;
    }

    /**
     * @return reveal skill state
     */
    public RevealSkill getRevealSkill() {
        return revealSkill;
    }

    /**
     * @return memory skill state
     */
    public MemorySkill getMemorySkill() {
        return memorySkill;
    }

    /**
     * @return quickness skill state
     */
    public QuicknessSkill getQuicknessSkill() {
        return quicknessSkill;
    }
}
