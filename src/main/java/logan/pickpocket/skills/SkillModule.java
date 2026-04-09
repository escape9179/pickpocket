package logan.pickpocket.skills;

import logan.pickpocket.user.PickpocketUser;

public class SkillModule {

    private PickpocketUser user;
    private SpeedSkill speedSkill;

    public SkillModule(PickpocketUser user) {
        this.user = user;
        this.speedSkill = new SpeedSkill();
    }

    public SpeedSkill getSpeedSkill() {
        return speedSkill;
    }
}
