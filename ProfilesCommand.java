package logan.pickpocket.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Tre on 12/15/2015.
 */
public class ProfilesCommand extends PickPocketCommand {

    @Override
    public boolean execute(Player player, Command command, String label, Object... args) {
        List<Profile> profiles = (List) args[0];

        player.sendMessage(ChatColor.GRAY + "Profiles: ");

        if (profiles.isEmpty()) return true;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < profiles.size(); i++) {
            stringBuilder.append(ChatColor.GRAY + profiles.get(i).getPlayer().getName() + " (" + ChatColor.WHITE + NumberFormat.getInstance().format(profiles.get(i).getExperience()) + ChatColor.GRAY + "XP), ");
        }

        player.sendMessage(stringBuilder.toString());

        return true;
    }
}
