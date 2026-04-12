package logan.pickpocket.skills;

/**
 * Static metadata for each supported pickpocket skill.
 */
public enum Skill {

    SPEED("Speed", "Determines how fast you can start rummaging through a player's inventory."),
    REVEAL("Reveal", "Determines how many items can be revealed each rummage stage."),
    MEMORY("Memory", "Determines how many previously revealed items are retained while expanding.");

    private String name;
    private String description;

     Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return display name for this skill
     */
    public String getName() {
        return name;
    }

    /**
     * @return description shown to users
     */
    public String getDescription() {
        return description;
    }
}
