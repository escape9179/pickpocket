package logan.pickpocket.profile;

/**
 * Created by Tre on 1/17/2016.
 */
public class StatisticModule {

    private final int maxLevel = 100;
    private final int stealAdder = 5;
    private final int stealsForMaxLevel = maxLevel * stealAdder * 10;
    private int lastStealAmount = 0;
    private int steals = 0;
    private int level = 0;

    int getLevel() {
        for (int i = 0; i < maxLevel * 10; i++) {
            if (steals >= lastStealAmount + 5) {
                level++;
                lastStealAmount = steals;
            }
        }

        return level;
    }

    int getMaxLevel() {
        return maxLevel;
    }

    int getSteals() {
        return steals;
    }

    int getStealsForMaxLevel() {
        return stealsForMaxLevel;
    }
}
