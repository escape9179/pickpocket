package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminExemptCommand extends BasicCommand<Player> {

    public AdminExemptCommand() {
        super("exempt", "pickpocket.admin.exempt", 0, 1,
                new String[0], SenderTarget.PLAYER, "admin",
                List.of(String.class),
                "/pickpocket admin exempt - Exempt yourself from being pick-pocketed.\n" +
                "/pickpocket admin exempt <name> - Exempt another player from being pick-pocketed.");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        if (args.length == 0) {
            PickpocketUser profile = PickpocketUser.get(sender);
            profile.setExempt(!profile.isExempt());
            sender.sendMessage(MessageConfiguration.getExemptStatusChangeMessage(profile.isExempt()));
        } else {
            Player otherPlayer = Bukkit.getPlayer(args[0]);
            if (otherPlayer == null) {
                sender.sendMessage(MessageConfiguration.getPlayerNotFoundMessage());
                return true;
            }
            PickpocketUser profile = PickpocketUser.get(otherPlayer);
            profile.setExempt(!profile.isExempt());
            sender.sendMessage(MessageConfiguration.getExemptStatusChangeOtherMessage(otherPlayer, profile.isExempt()));
            otherPlayer.sendMessage(MessageConfiguration.getExemptStatusChangeMessage(profile.isExempt()));
        }
        return true;
    }
}
