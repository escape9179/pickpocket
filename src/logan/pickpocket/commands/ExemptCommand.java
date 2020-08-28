package logan.pickpocket.commands;

import logan.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class ExemptCommand implements PickpocketCommand {


    @Override
    public <T> void execute(Player player, List<PickpocketUser> profiles, T... objects) {
        if (objects.length == 0) {
            PickpocketUser profile = Profiles.get(player);
            boolean exemptStatus = !profile.getProfileConfiguration().getExemptSectionValue();
            profile.getProfileConfiguration().setExemptSection(exemptStatus);
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.EXEMPT_STATUS_CHANGE_KEY, Boolean.toString(exemptStatus)));
        } else {
            Player otherPlayer = Bukkit.getPlayer(objects[0].toString());
            PickpocketUser otherPlayerProfile = Profiles.get(otherPlayer);
            boolean exemptStatus = !otherPlayerProfile.getProfileConfiguration().getExemptSectionValue();
            otherPlayerProfile.getProfileConfiguration().setExemptSection(exemptStatus);
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.EXEMPT_STATUS_CHANGE_OTHER_KEY, otherPlayer, Boolean.toString(exemptStatus)));
            otherPlayer.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.EXEMPT_STATUS_CHANGE_KEY, Boolean.toString(exemptStatus)));
        }
    }
}
