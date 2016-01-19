package logan.pickpocket.profile;

import logan.pickpocket.main.PickpocketItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tre on 1/18/2016.
 */
public class PickpocketItemLoader {
    public Map<PickpocketItem, Integer> loadPickpocketItemsFromYamlConfiguration(ProfileConfiguration profileConfiguration) {
        List<?> pickpocketSaves = profileConfiguration.getYamlConfiguration().getList(profileConfiguration.getItemSection());
        Map<PickpocketItem, Integer> pickpocketItems = new HashMap<>();
        if (pickpocketSaves == null || pickpocketSaves.isEmpty()) return pickpocketItems;

        String[][] data = new String[pickpocketSaves.size()][2];
        for (int i = 0; i < pickpocketSaves.size(); i++) {
            data[i][0] = pickpocketSaves.get(i).toString().split(",")[0];
            data[i][1] = pickpocketSaves.get(i).toString().split(",")[1];
        }

        for (int i = 0; i < data.length; i++) {
            pickpocketItems.put(PickpocketItem.getPickpocketItemByName(data[i][0]), Integer.valueOf(data[i][1]));
        }

        return pickpocketItems;
    }

    public void writePickpocketItemsToYamlConfiguration(ProfileConfiguration profileConfiguration, Map<PickpocketItem, Integer> pickpocketItemIntegerMap) {
        List<String> listSave = new ArrayList<>();
        for (PickpocketItem pickpocketItem : pickpocketItemIntegerMap.keySet()) {
            listSave.add(pickpocketItem.getRawName() + "," + pickpocketItemIntegerMap.get(pickpocketItem));
        }

        profileConfiguration.getYamlConfiguration().set(profileConfiguration.getItemSection(), listSave);
    }
}
