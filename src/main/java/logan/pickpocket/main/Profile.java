package logan.pickpocket.main;

import java.util.HashMap;
import java.util.Map;

public class Profile {

    private final String name;
    private final Map<String, String> properties;

    public Profile(String name) {
        this.name = name;
        this.properties = new HashMap<>();
        properties.put("cooldown", "10");
        properties.put("canUseFishingRod", "false");
        properties.put("numberOfRummageItems", "4");
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public int getCooldown() {
        return Integer.parseInt(properties.get("cooldown"));
    }

    public void setCooldown(int value) {
        properties.put("cooldown", String.valueOf(value));
    }

    public boolean canUseFishingRod() {
        return Boolean.parseBoolean(properties.get("canUseFishingRod"));
    }

    public void setCanUseFishingRod(boolean value) {
        properties.put("canUseFishingRod", String.valueOf(value));
    }

    public int getMaxRummageItems() {
        return Integer.parseInt(properties.get("numberOfRummageItems"));
    }

    public void setMaxRummageItems(int value) {
        properties.put("numberOfRummageItems", String.valueOf(value));
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Profile otherProfile) {
            return otherProfile.name.equals(this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean save() {
        PickpocketPlugin.getProfileConfiguration().saveProfile(this);
        return true;
    }
}
