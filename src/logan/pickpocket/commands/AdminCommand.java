package logan.pickpocket.commands;

import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class AdminCommand implements PickpocketCommand
{
    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects)
    {
        Profile profile = Profiles.get(player);
        boolean value   = profile.getProfileConfiguration().getAdminSectionValue();
        Profiles.get(player).getProfileConfiguration().setAdminSection(value = !value);
        player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.ADMIN_STATUS_CHANGE_KEY, Boolean.toString(value)));
    }
}
