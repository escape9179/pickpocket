package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.Profile;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.stream.Collectors;

public class ProfileViewCommand extends BasicCommand<Player> {

    public ProfileViewCommand() {
        super("view", "pickpocket.profile.view", 0, 0,
                new String[0], SenderTarget.PLAYER, "profile",
                Collections.emptyList(), null);
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        Profile profile = PickpocketUser.get(sender).findThiefProfile();
        if (profile == null) {
            sender.sendMessage(MessageConfiguration.getProfileNotAssignedMessage());
            return true;
        }
        String propsString = profile.getProperties().entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
        sender.sendMessage("name: " + profile.getName() + "\n" + propsString);
        return true;
    }
}
