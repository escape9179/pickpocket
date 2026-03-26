package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminCommand extends BasicCommand<Player> {

    public AdminCommand() {
        super("admin", "pickpocket.admin", 1, 2,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                List.of(String.class, String.class),
                "/pickpocket admin bypass [name]\n" +
                "/pickpocket admin exempt [name]\n" +
                "/pickpocket admin notify");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        PickpocketUser user = PickpocketUser.get(sender);
        user.setAdmin(!user.isAdmin());
        sender.sendMessage(MessageConfiguration.getAdminStatusChangeMessage(user.isAdmin()));
        return false;
    }
}
