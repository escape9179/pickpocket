package logan.pickpocket.commands;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.profile.Profile;
import logan.pickpocket.main.Profiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Tre on 1/10/2016.
 */
public class AdminCommand implements PickpocketCommand {

    private Pickpocket pickpocket;

    public AdminCommand(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
    }

    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects) {
        if (player.isOp() || player.hasPermission(Pickpocket.PICKPOCKET_ADMIN)) {
            Profile profile = Profiles.get(player, profiles, pickpocket);
            boolean value = profile.getProfileConfiguration().getAdminSectionValue();
            Profiles.get(player, profiles, pickpocket).getProfileConfiguration().setAdminSection(value = !value);
            player.sendMessage(ChatColor.GRAY + "Pickpocket Admin status set to " + value + ".");
        }
    }
}
