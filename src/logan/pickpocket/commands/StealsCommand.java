package logan.pickpocket.commands;

import logan.pickpocket.main.Pickpocket;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Tre on 12/25/2015.
 */
public class StealsCommand implements PickpocketCommand {

    private Pickpocket pickpocket;

    public StealsCommand(Pickpocket pickpocket) {
        this.pickpocket = pickpocket;
    }

    @Override
    public void execute(Player player, List<Profile> profiles, Object... objects) {
        Profile profile = Profiles.get(player, profiles,pickpocket);
        profile.getPlayer().sendMessage(ChatColor.GRAY + "You've stole " + ChatColor.GREEN + NumberFormat.getInstance().format(profile.getPickpocketItemModule().getSteals()) + " times.");
        profile.getPlayer().sendMessage(ChatColor.GRAY + "Level " + ChatColor.YELLOW + profile.getStatisticModule().getLevel());
    }
}
