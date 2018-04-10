package logan.pickpocket.commands;

import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Tre on 12/15/2015.
 */
public class ProfilesCommand implements PickpocketCommand {

    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... objects) {
        player.sendMessage(ChatColor.GRAY + "Profiles: ");

        if (profiles.isEmpty()) return;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < profiles.size(); i++) {
            stringBuilder.append(ChatColor.GRAY);
            stringBuilder.append(profiles.get(i).getPlayer().getName());
            stringBuilder.append(" (");
            stringBuilder.append(ChatColor.WHITE);
            stringBuilder.append(NumberFormat.getInstance().format(profiles.get(i).getPickpocketItemModule().getSteals()));
            stringBuilder.append(ChatColor.GRAY);
            stringBuilder.append("), ");
        }

        player.sendMessage(stringBuilder.toString());
    }
}
