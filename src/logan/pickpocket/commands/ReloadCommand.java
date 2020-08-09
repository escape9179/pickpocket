package logan.pickpocket.commands;

import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand implements PickpocketCommand {
    @Override
    public <T> void execute(Player player, List<Profile> profiles, T... args) {
        PickpocketPlugin.reloadConfiguration();
        player.sendMessage(ChatColor.GREEN + "Reloaded Pickpocket configuration.");
    }
}
