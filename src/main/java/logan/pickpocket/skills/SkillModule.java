package logan.pickpocket.skills;

import logan.pickpocket.user.PickpocketUser;

public class SkillModule {

    private SpeedSkill speedSkill;
    private RevealSkill revealSkill;
    private MemorySkill memorySkill;

    public SkillModule(PickpocketUser user) {
        this.speedSkill = new SpeedSkill();
        this.revealSkill = new RevealSkill();
        this.memorySkill = new MemorySkill();
    }

    public SpeedSkill getSpeedSkill() {
        return speedSkill;
    }

    public RevealSkill getRevealSkill() {
        return revealSkill;
    }

    public MemorySkill getMemorySkill() {
        return memorySkill;
    }
}
