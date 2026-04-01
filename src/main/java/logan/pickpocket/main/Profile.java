package logan.pickpocket.main;

import java.util.LinkedHashMap;
import java.util.Map;

public class Profile {

    private final String name;
    private int cooldown;
    private boolean canUseFishingRod;
    private int numberOfRummageItems;

    public Profile(String name) {
        this.name = name;
        this.cooldown = 10;
        this.canUseFishingRod = false;
        this.numberOfRummageItems = 4;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int value) {
        this.cooldown = value;
    }

    public boolean canUseFishingRod() {
        return canUseFishingRod;
    }

    public void setCanUseFishingRod(boolean value) {
        this.canUseFishingRod = value;
    }

    public int getMaxRummageItems() {
        return numberOfRummageItems;
    }

    public void setMaxRummageItems(int value) {
        this.numberOfRummageItems = value;
    }

    /**
     * Returns a map view of all properties (for serialization and display).
     */
    public Map<String, String> getProperties() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("cooldown", String.valueOf(cooldown));
        map.put("canUseFishingRod", String.valueOf(canUseFishingRod));
        map.put("numberOfRummageItems", String.valueOf(numberOfRummageItems));
        return map;
    }

    /**
     * Sets a property by name. Returns true if the property was recognized.
     */
    public boolean setProperty(String key, String value) {
        switch (key) {
            case "cooldown" -> this.cooldown = Integer.parseInt(value);
            case "canUseFishingRod" -> this.canUseFishingRod = Boolean.parseBoolean(value);
            case "numberOfRummageItems" -> this.numberOfRummageItems = Integer.parseInt(value);
            default -> { return false; }
        }
        return true;
    }

    /**
     * Gets a property value by name, or null if not recognized.
     */
    public String getProperty(String key) {
        return getProperties().get(key);
    }

    /**
     * Returns true if the given key is a valid property name.
     */
    public boolean hasProperty(String key) {
        return getProperties().containsKey(key);
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
