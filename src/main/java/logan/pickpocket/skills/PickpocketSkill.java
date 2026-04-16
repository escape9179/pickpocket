package logan.pickpocket.skills;

/**
 * Contract for mutable skill progression state.
 */
public interface PickpocketSkill {
    public static final int MAX_LEVEL = 100;

    /**
     * @return skill name
     */
    public String getName();

    /**
     * @return skill description
     */
    public String getDescription();

    /**
     * @return maximum allowed level
     */
    public int getMaxLevel();

    /**
     * @return current level
     */
    public int getLevel();

    /**
     * @return current experience value
     */
    public int getExp();

    /**
     * Sets the skill level.
     *
     * @param level target level
     */
    public void setLevel(int level);

    /**
     * Sets the current experience value.
     *
     * @param exp target experience
     */
    public void setExp(int exp);

    /**
     * Adds experience and handles level progression.
     *
     * @param exp experience to add
     */
    public void addExp(int exp);

    /**
     * Removes experience and handles level regression.
     *
     * @param exp experience to remove
     */
    public void removeExp(int exp);
}
