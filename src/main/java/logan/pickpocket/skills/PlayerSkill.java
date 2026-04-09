package logan.pickpocket.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PlayerSkill implements PickpocketSkill, ConfigurationSerializable {
    private Skill skill;
    private int level;
    private int exp;

    public PlayerSkill(Skill skill) {
        this.skill = skill;
        this.level = 0;
        this.exp = 0;
    }
    
    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    @Override
    public String getName() {
        return skill.getName();
    }

    @Override
    public String getDescription() {
        return skill.getDescription();
    }

    @Override
    public int getMaxLevel() {
        return PickpocketSkill.MAX_LEVEL;
    }

    @Override
    public void setLevel(int level) {
        if (level < 0) {
            this.level = 0;
        } else if (level > getMaxLevel()) {
            this.level = getMaxLevel();
        } else {
            this.level = level;
        }
    }

    @Override
    public void setExp(int exp) {
        if (exp < 0) {
            this.exp = 0;
        } else {
            this.exp = exp;
        }
    }

    @Override
    public void addExp(int exp) {
        if (exp < 0) return;
        this.exp += exp;
        while (this.exp >= getExpNeededForNextLevel() && this.level < getMaxLevel()) {
            this.exp -= getExpNeededForNextLevel();
            this.level++;
        }
        if (this.level >= getMaxLevel()) {
            this.level = getMaxLevel();
            this.exp = 0;
        }
    }

    @Override
    public void removeExp(int exp) {
        if (exp < 0) return;
        this.exp -= exp;
        while (this.exp < 0 && this.level > 0) {
            this.level--;
            this.exp += getExpNeededForNextLevel();
        }
        if (this.exp < 0) {
            this.exp = 0;
        }
    }

    /**
     * Gets the EXP needed to reach the next level from the current one.
     * For now, always returns 100.
     */
    private int getExpNeededForNextLevel() {
        return 100; //TODO: Implement this
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("skill", skill.getName());
        map.put("level", level);
        map.put("exp", exp);
        return map;
    }

    @Override
    public String toString() {
        return "PlayerSkill{skill=" + skill.toString() + ", level=" + level + ", exp=" + exp + ", maxLevel=" + MAX_LEVEL + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerSkill other = (PlayerSkill) obj;
        return skill.equals(other.skill) && level == other.level && exp == other.exp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skill, level, exp);
    }
}
