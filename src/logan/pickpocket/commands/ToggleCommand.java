package logan.pickpocket.commands;

import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand implements PickpocketCommand
{
    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... args)
    {
        if (!player.hasPermission(PickpocketPlugin.PICKPOCKET_TOGGLE))
        {
            player.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        Profile profile = profiles.get(0);

        profile.setParticipating(!profile.isParticipating());

        player.sendMessage(ChatColor.GRAY + "Pick-pocketing is now " + (profile.isParticipating() ? "enabled." : "disabled."));
    }
}
