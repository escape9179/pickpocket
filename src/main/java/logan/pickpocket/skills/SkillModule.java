package logan.pickpocket.skills;

import logan.pickpocket.user.PickpocketUser;

public class SkillModule {

    private PickpocketUser user;
    private SpeedSkill speedSkill;
    private RevealSkill revealSkill;
    private MemorySkill memorySkill;

    public SkillModule(PickpocketUser user) {
        this.user = user;
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
