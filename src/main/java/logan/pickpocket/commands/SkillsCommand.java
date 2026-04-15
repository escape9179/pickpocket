package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.pickpocket.skills.PlayerSkill;
import logan.pickpocket.user.PickpocketUser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SkillsCommand extends BasicCommand<Player> {

    private static final String MENU_TITLE = ChatColor.DARK_GREEN + "Pickpocket Skills";

    public SkillsCommand() {
        super("skills", "pickpocket.skill.view", 0, 0,
                new String[] { "skill" }, SenderTarget.PLAYER, "pickpocket",
                List.of(),
                "Usage:\n/pickpocket skills");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        PickpocketUser user = PickpocketUser.get(sender);

        PlayerInventoryMenu menu = new PlayerInventoryMenu(MENU_TITLE, 3);
        fillBackground(menu);
        menu.addItem(10, createSkillItem(Material.SUGAR, user.getSpeedSkill()));
        menu.addItem(12, createSkillItem(Material.COMPASS, user.getRevealSkill()));
        menu.addItem(14, createSkillItem(Material.BOOK, user.getMemorySkill()));
        menu.addItem(16, createSkillItem(Material.RABBIT_FOOT, user.getQuicknessSkill()));
        menu.show(sender);

        return true;
    }

    private void fillBackground(PlayerInventoryMenu menu) {
        for (int slot = 0; slot < menu.getSize(); slot++) {
            menu.addItem(slot, new MenuItem(" ", new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
        }
    }

    private MenuItem createSkillItem(Material material, PlayerSkill skill) {
        String progressLine = skill.getLevel() >= skill.getMaxLevel()
                ? ChatColor.GREEN + "Max level reached"
                : ChatColor.GRAY + "EXP: " + ChatColor.WHITE + skill.getExp() + ChatColor.GRAY + " / 100";

        return new MenuItem(skill.getName(), new ItemStack(material))
                .setLore(
                        ChatColor.GRAY + skill.getDescription(),
                        "",
                        ChatColor.GRAY + "Level: " + ChatColor.GOLD + skill.getLevel() + ChatColor.GRAY + " / " + skill.getMaxLevel(),
                        progressLine);
    }
}
