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
public class BypassCommand implements PickpocketCommand {

    @Override
    public <T> void execute(Player player, List<PickpocketUser> profiles, T... objects) {

        //TODO Fix array index out of bounds here
        if (objects.length == 0) {
            PickpocketUser profile = Profiles.get(player);
            boolean exemptStatus = !profile.getProfileConfiguration().getBypassSectionValue();
            profile.getProfileConfiguration().setBypassSection(exemptStatus);
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.BYPASS_STATUS_CHANGE_KEY, Boolean.toString(exemptStatus)));
        } else {
            Player otherPlayer = Bukkit.getPlayer(objects[0].toString());
            if (otherPlayer == null) {
                player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.PLAYER_NOT_FOUND_KEY));
                return;
            }
            PickpocketUser otherPlayerProfile = Profiles.get(otherPlayer);
            boolean exemptStatus = !otherPlayerProfile.getProfileConfiguration().getExemptSectionValue();
            player.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.BYPASS_STATUS_CHANGE_OTHER_KEY, otherPlayer, Boolean.toString(exemptStatus)));
            otherPlayer.sendMessage(PickpocketPlugin.getMessageConfiguration().getMessage(MessageConfiguration.BYPASS_STATUS_CHANGE_KEY, Boolean.toString(exemptStatus)));
        }
    }
}
