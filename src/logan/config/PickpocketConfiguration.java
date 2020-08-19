package logan.config;

import logan.pickpocket.main.PickpocketPlugin;

import java.util.List;

public class PickpocketConfiguration {

    private static final String caughtChanceKey = "caught-chance";
    private static final String minigameRollRateKey = "minigame-roll-rate";
    private static final String cooldownTimeKey = "cooldown-time";
    private static final String pickpocketToggleKey = "allow-pickpocket-toggling";
    private static final String statusOnInteractKey = "show-status-on-interact";
    private static final String statusOnLoginKey = "show-status-on-login";
    private static final String disabledItemsKey = "disabled-items";

    private static CommentedConfig config;

    public static void init() {
        config = new CommentedConfig(PickpocketPlugin.getInstance().getResource("config.yml"), PickpocketPlugin.getInstance().getDataFolder().getPath() + "/config.yml");
        config.save();
    }

    public static double getCaughtChance() {
        return config.getConfiguration().getDouble(caughtChanceKey);
    }

    public static int getMinigameRollRate() {
        return config.getConfiguration().getInt(minigameRollRateKey);
    }

    public static List<String> getDisabledItems() {
        return config.getConfiguration().getStringList(disabledItemsKey);
    }

    public static boolean isShowStatusOnInteractEnabled() {
        return config.getConfiguration().getBoolean(statusOnInteractKey);
    }

    public static boolean isShowStatusOnLoginEnabled() {
        return config.getConfiguration().getBoolean(statusOnLoginKey);
    }

    public static int getCooldownTime() {
        return config.getConfiguration().getInt(cooldownTimeKey);
    }

    public static void reloadConfiguration() {
        config.loadConfiguration();
    }
}
