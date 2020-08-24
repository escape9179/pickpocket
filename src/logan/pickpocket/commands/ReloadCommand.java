package logan.pickpocket.commands;

import logan.config.MessageConfiguration;
import logan.config.PickpocketConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand implements PickpocketCommand {
    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... args) {
        PickpocketConfiguration.reloadConfiguration();
        player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.RELOAD_NOTIFICATION_KEY));
    }
}
