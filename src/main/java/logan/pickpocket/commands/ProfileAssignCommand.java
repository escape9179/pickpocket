package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.config.MessageConfiguration;
import logan.pickpocket.main.PickpocketPlugin;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileAssignCommand extends BasicCommand<CommandSender> {

    public ProfileAssignCommand() {
        super("assign", "pickpocket.profile.assign", 2, 2,
                new String[0], SenderTarget.BOTH, "profile",
                List.of(String.class, String.class),
                "Usage:\n/pickpocket profile assign <profile> <player>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return new ArrayList<>(PickpocketPlugin.getProfileConfiguration().getProfileNames());
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public boolean run(CommandSender sender, String[] args, Object data) {
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage(MessageConfiguration.getPlayerNotFoundMessage());
            return true;
        }
        PickpocketUser user = PickpocketUser.get(targetPlayer);
        if (user.assignThiefProfile(args[0])) {
            sender.sendMessage(MessageConfiguration.getProfileAssignSuccessMessage(args[0], user.getBukkitPlayer()));
        } else {
            sender.sendMessage(MessageConfiguration.getProfileAssignFailureMessage(args[0], user.getBukkitPlayer()));
        }
        return true;
    }
}
