package logan.pickpocket.skills;

public interface PickpocketSkill {
    public static final int MAX_LEVEL = 100;
    public String getName();
    public String getDescription();
    public int getMaxLevel();
    public int getLevel();
    public int getExp();
    public void setLevel(int level);
    public void setExp(int exp);
    public void addExp(int exp);
    public void removeExp(int exp);
}
