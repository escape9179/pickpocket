package logan.pickpocket.listeners;

import logan.pickpocket.main.Profiles;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Profile playerProfile = Profiles.get(event.getPlayer());

        // Check if the player is a predator
        if (playerProfile.isPredator()) {
            Profile victimProfile = Profiles.get(playerProfile.getVictim());
            if (playerProfile.isPlayingMinigame()) {
                playerProfile.getMinigameModule().stopMinigame();
            }
            if (playerProfile.isRummaging()) {
                playerProfile.getPlayer().closeInventory();
                playerProfile.setRummaging(false);
            }
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot move whilst pick-pocketing.");
            playerProfile.setVictim(null);
            victimProfile.setPredator(null);
            return;
        }

        // Check if the player is a victim
        if (playerProfile.isVictim()) {
            System.out.println("The player is a victim.");
            Profile predatorProfile = Profiles.get(playerProfile.getPredator());
            if (predatorProfile.isPlayingMinigame()) {
                predatorProfile.getMinigameModule().stopMinigame();
                System.out.println("Stopping predator mini-game.");
            }
            if (predatorProfile.isRummaging()) {
                predatorProfile.getPlayer().closeInventory();
                predatorProfile.setRummaging(false);
            }
            playerProfile.getPredator().sendMessage(ChatColor.RED + "The player moved.");
            playerProfile.setPredator(null);
            predatorProfile.setVictim(null);
        }
    }
}
