package logan.pickpocket.config;

import logan.api.config.CommentedConfiguration;
import logan.pickpocket.main.PickpocketPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class PickpocketConfiguration extends CommentedConfiguration {

    private static final String loseMoney = "lose-money-on-pickpocket";
    private static final String moneyLost = "pickpocket-money-lost";
    private static final String minigameRollRateKey = "minigame-roll-rate";
    private static final String cooldownTimeKey = "cooldown-time";
    private static final String pickpocketToggleKey = "allow-pickpocket-toggling";
    private static final String statusOnInteractKey = "show-status-on-interact";
    private static final String statusOnLoginKey = "show-status-on-login";
    private static final String disabledItemsKey = "disabled-items";
    private static final String foreignTownTheftKey = "foreign-town-theft";
    private static final String sameTownTheftKey = "same-town-theft";

    public PickpocketConfiguration() {
        super(new File(PickpocketPlugin.getInstance().getDataFolder(), "config.yml"));
    }

    public void create() {
        createKeyIfNoneExists(loseMoney, false);
        createKeyIfNoneExists(moneyLost, 0.025);
        createKeyIfNoneExists(minigameRollRateKey, 20);
        createKeyIfNoneExists(cooldownTimeKey, 10);
        createKeyIfNoneExists(pickpocketToggleKey, true);
        createKeyIfNoneExists(statusOnInteractKey, true);
        createKeyIfNoneExists(statusOnLoginKey, true);
        createKeyIfNoneExists(disabledItemsKey, Collections.singletonList("cake"));
        createKeyIfNoneExists(foreignTownTheftKey, false);
        createKeyIfNoneExists(sameTownTheftKey, false);

        addCommentToKey(loseMoney, "Whether or not predators should take money from", "victims when pick-pocketing.");
        addCommentToKey(moneyLost, "The percentage of money taken by the predator after", "successfully pick-pocketing another player.");
        addCommentToKey(minigameRollRateKey, "The time in ticks a user has before the", "mini-game inventory slots are randomized again.");
        addCommentToKey(cooldownTimeKey, "The time the player must wait in seconds", "between pick-pocketing attempts.", "An attempt is when a player successfully", "pick-pockets another player.");
        addCommentToKey(pickpocketToggleKey, "Allow players to disable pick-pocketing", "for themselves. This will also disallow others", "from pick-pocketing them.");
        addCommentToKey(statusOnInteractKey, "Whether or not to show a players the", "pick-pocket status message when they attempt", "to pick-pocket another player whilst they, or the", "victim has pick-pocketing disabled.");
        addCommentToKey(statusOnLoginKey, "Whether or not to show a players pick-pocket status when logging in.");
        addCommentToKey(disabledItemsKey, "Items that can't be stolen and therefore, won't show", "up in the rummage GUI. A list of Minecraft IDs can be found", "at www.deadmap.com/idlist");
        addCommentToKey(foreignTownTheftKey, "Whether players should be able to steal from people in their own town.");
        addCommentToKey(sameTownTheftKey, "Whether players should be able to steal from their own town-folk");

        save();
    }

    public boolean getIsMoneyLostEnabled() {
        return getConfiguration().getBoolean(loseMoney);
    }

    public double getMoneyLostPercentage() {
        return getConfiguration().getDouble(moneyLost);
    }

    public int getMinigameRollRate() {
        return getConfiguration().getInt(minigameRollRateKey);
    }

    public List<String> getDisabledItems() {
        return getConfiguration().getStringList(disabledItemsKey);
    }

    public boolean isShowStatusOnInteractEnabled() {
        return getConfiguration().getBoolean(statusOnInteractKey);
    }

    public boolean isShowStatusOnLoginEnabled() {
        return getConfiguration().getBoolean(statusOnLoginKey);
    }

    public int getCooldownTime() {
        return getConfiguration().getInt(cooldownTimeKey);
    }

    public boolean isForeignTownTheftEnabled() {
        return getConfiguration().getBoolean(foreignTownTheftKey);
    }

    public boolean isSameTownTheftEnabled() {
        return getConfiguration().getBoolean(sameTownTheftKey);
    }
}
