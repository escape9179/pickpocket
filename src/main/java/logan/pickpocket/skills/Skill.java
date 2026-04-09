package logan.pickpocket.skills;

public enum Skill {

    SPEED("Speed", "Determines how fast you can start rummaging through a player's inventory.");

    private String name;
    private String description;

     Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
