package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminBypassCommand extends BasicCommand<Player> {

    public AdminBypassCommand() {
        super("bypass", "pickpocket.admin.bypass", 0, 1,
                new String[]{"admin"}, SenderTarget.PLAYER, "admin",
                List.of(String.class),
                "/pickpocket admin bypass - Enable cooldown delay bypass for yourself.\n" +
                "/pickpocket admin bypass [name] - Enable cooldown delay bypass for another person.");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        if (args.length == 0) {
            PickpocketUser profile = PickpocketUser.get(sender);
            profile.setBypassing(!profile.isBypassing());
            sender.sendMessage(MessageConfiguration.getBypassStatusChangeMessage(profile.isBypassing()));
        } else {
            Player otherPlayer = Bukkit.getPlayer(args[0]);
            if (otherPlayer == null) {
                sender.sendMessage(MessageConfiguration.getPlayerNotFoundMessage());
                return true;
            }
            PickpocketUser otherPlayerProfile = PickpocketUser.get(otherPlayer);
            otherPlayerProfile.setBypassing(!otherPlayerProfile.isBypassing());
            sender.sendMessage(MessageConfiguration.getBypassStatusChangeOtherMessage(otherPlayer, otherPlayerProfile.isBypassing()));
            otherPlayer.sendMessage(MessageConfiguration.getBypassStatusChangeMessage(otherPlayerProfile.isBypassing()));
        }
        return true;
    }
}
