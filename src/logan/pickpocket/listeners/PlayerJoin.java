package logan.pickpocket.listeners;

import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Tre on 12/28/2015.
 */
public class PlayerJoin implements Listener
{
    public PlayerJoin()
    {
        PickpocketPlugin.registerListener(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {

        Player  player  = event.getPlayer();
        Profile profile = Profiles.get(player);

        if (profile.isParticipating())
        {
            player.sendMessage(ChatColor.GRAY + "You are currently participating in pick-pocketing.");
            player.sendMessage(ChatColor.GRAY + "Use '/pickpocket toggle' to disable pick-pocketing.");
        }
        else
        {
            player.sendMessage(ChatColor.GRAY + "You are currently not participating in pick-pocketing.");
            player.sendMessage(ChatColor.GRAY + "Use '/pickpocket toggle' to enable pick-pocketing.");
        }
    }
}
