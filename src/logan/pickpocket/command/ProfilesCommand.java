package logan.pickpocket.command;

import logan.pickpocket.main.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Tre on 12/15/2015.
 */
public class ProfilesCommand implements PickpocketCommand {

    @Override
    public void execute(Player player, List<Profile> profiles, Object... objects) {
        player.sendMessage(ChatColor.GRAY + "Profiles: ");

        if (profiles.isEmpty()) return;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < profiles.size(); i++) {
            stringBuilder.append(ChatColor.GRAY + profiles.get(i).getPlayer().getName() + " (" + ChatColor.WHITE + NumberFormat.getInstance().format(profiles.get(i).getTimesStolen()) + ChatColor.GRAY + "), ");
        }

        player.sendMessage(stringBuilder.toString());
    }
}
