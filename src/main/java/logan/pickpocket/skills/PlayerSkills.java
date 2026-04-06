package logan.pickpocket.skills;

import logan.pickpocket.user.PickpocketUser;

public class PlayerSkills {

    private final PickpocketUser user;

    public PlayerSkills(PickpocketUser user) {
        this.user = user;
    }

    /**
     * Gets the level of the specified skill.
     * Default level is 0 if not previously set.
     */
    public int getSkillLevel(Skills skill) {
        return user.getConfiguration().getInt("skills." + skill.name() + ".level", 0);
    }

    /**
     * Sets the level of the specified skill and saves the user configuration.
     * Enforces the maximum level cap.
     */
    public void setSkillLevel(Skills skill, int level) {
        if (level < 0)
            level = 0;

        // As defined by the specific skills, normally max 100
        int maxLevel = 100;
        if (skill == Skills.SPEED) {
            maxLevel = SpeedSkill.MAX_LEVEL;
        }

        if (level > maxLevel)
            level = maxLevel;

        user.getConfiguration().set("skills." + skill.name() + ".level", level);
        user.save();
    }

    /**
     * Gets the current experience points for the given skill.
     */
    public int getSkillExp(Skills skill) {
        return user.getConfiguration().getInt("skills." + skill.name() + ".exp", 0);
    }

    /**
     * Sets the current experience points for the given skill and saves.
     */
    public void setSkillExp(Skills skill, int exp) {
        user.getConfiguration().set("skills." + skill.name() + ".exp", exp);
        user.save();
    }

    /**
     * Adds experience to a skill, automatically leveling it up if it passes a
     * threshold.
     * For now, we will use a simple linear progression: 100 exp per level.
     */
    public void addExperience(Skills skill, int amount) {
        int currentExp = getSkillExp(skill);
        int currentLevel = getSkillLevel(skill);

        if (currentLevel >= 100)
            return; // Reached max level

        int newExp = currentExp + amount;
        int expNeeded = getExpRequiredForNextLevel(currentLevel);

        while (newExp >= expNeeded && currentLevel < 100) {
            newExp -= expNeeded;
            currentLevel++;
            expNeeded = getExpRequiredForNextLevel(currentLevel);

            // Send optional level up message
            user.sendMessage(
                    "&a&lLevel Up! &7Your &e" + skill.name() + " &7skill is now level &e" + currentLevel + "&7!");
        }

        if (currentLevel >= 100) { // Safety check after loop
            currentLevel = 100;
            newExp = 0;
        }

        user.getConfiguration().set("skills." + skill.name() + ".level", currentLevel);
        user.getConfiguration().set("skills." + skill.name() + ".exp", newExp);
        user.save();
    }

    /**
     * Calculates the experience required to advance to the next level.
     * Current calculation: 100 xp per level.
     */
    private int getExpRequiredForNextLevel(int currentLevel) {
        return 100 + (currentLevel * 50); // Progressively harder
    }
}
