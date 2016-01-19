package logan.pickpocket.profile;

import logan.pickpocket.main.PickpocketItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tre on 1/17/2016.
 */
public class PickpocketItemModule {
    private Map<PickpocketItem, Integer> pickpocketItemIntegerMap;

    public PickpocketItemModule() {
        pickpocketItemIntegerMap = new HashMap<>();
    }

    public int getStealsOf(PickpocketItem pickpocketItem) {
        return pickpocketItemIntegerMap.get(pickpocketItem);
    }

    public int getSteals() {
        int count = 0;
        for (PickpocketItem pickpocketItem : pickpocketItemIntegerMap.keySet()) count += pickpocketItemIntegerMap.get(pickpocketItem);
        return count;
    }

    public Map<PickpocketItem, Integer> getPickpocketItemIntegerMap() {
        return pickpocketItemIntegerMap;
    }

    public boolean hasPickpocketItem(PickpocketItem pickpocketItem) {
        if (pickpocketItemIntegerMap.containsKey(pickpocketItem)) return true;
        return false;
    }

    public boolean addPickpocketItem(PickpocketItem pickpocketItem) {
        if (!pickpocketItemIntegerMap.containsKey(pickpocketItem)) {
            pickpocketItemIntegerMap.put(pickpocketItem, 1);
            return true;
        } else {
            pickpocketItemIntegerMap.put(pickpocketItem, pickpocketItemIntegerMap.get(pickpocketItem) + 1);
            return false;
        }
    }

    public void setPickpocketItemIntegerMap(Map<PickpocketItem, Integer> pickpocketItemIntegerMap) {
        this.pickpocketItemIntegerMap = pickpocketItemIntegerMap;
    }
}
