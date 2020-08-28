package logan.pickpocket.commands;

import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand implements PickpocketCommand {
    @Override
    public <T> void execute(Player player, List<PickpocketUser> profiles, T... args) {
        PickpocketUser profile = Profiles.get(player);

        profile.setParticipating(!profile.isParticipating());

        player.sendMessage(profile.isParticipating() ?
                PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_TOGGLE_ON_KEY) :
                PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PICKPOCKET_TOGGLE_OFF_KEY));
    }
}
