package logan.pickpocket.commands;

import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.pickpocket.skills.PlayerSkill;
import logan.pickpocket.user.PickpocketUser;

public class SetSkillCommand extends BasicCommand<Player> {

    private static final List<String> SKILL_SUGGESTIONS = List.of("speed", "reveal", "memory", "quickness");
    private static final List<String> LEVEL_SUGGESTIONS = List.of("0", "1", "10", "50", "100");

    public SetSkillCommand() {
        super("setskill", "pickpocket.skill.set", 2, 2,
                new String[0], SenderTarget.PLAYER, "pickpocket",
                List.of(String.class, String.class),
                "/pickpocket setskill <skill name> <level>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return SKILL_SUGGESTIONS;
        }
        if (args.length == 2) {
            return LEVEL_SUGGESTIONS;
        }
        return List.of();
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        PickpocketUser user = PickpocketUser.get(sender);
        PlayerSkill playerSkill = resolveSkill(user, args[0]);
        if (playerSkill == null) {
            sender.sendMessage("Unknown skill. Valid skills: speed, reveal, memory, quickness.");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("Usage:\n/pickpocket setskill <skill name> <level>");
            return true;
        }

        playerSkill.setLevel(level);

        user.persistSkillStats();
        user.save();

        sender.sendMessage("Set " + playerSkill.getName() + " to level " + playerSkill.getLevel() + ".");
        return true;
    }

    private PlayerSkill resolveSkill(PickpocketUser user, String rawSkillName) {
        String normalizedSkillName = rawSkillName.toLowerCase(Locale.ROOT);
        switch (normalizedSkillName) {
            case "speed":
                return user.getSpeedSkill();
            case "reveal":
                return user.getRevealSkill();
            case "memory":
                return user.getMemorySkill();
            case "quickness":
                return user.getQuicknessSkill();
            default:
                return null;
        }
    }
}
