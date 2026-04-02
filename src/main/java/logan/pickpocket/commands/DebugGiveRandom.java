package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.api.util.FunctionUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DebugGiveRandom extends BasicCommand<Player> {

    private static final Material[] MATERIALS = Material.values();

    public DebugGiveRandom() {
        super("giverandom", "pickpocket.debug.giverandom", 1, 1,
                new String[] { "gr" }, SenderTarget.PLAYER, "debug",
                List.of(String.class),
                "/pickpocket debug giverandom <amount>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return List.of("1", "5", "10", "32", "64");
        }
        return List.of();
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        int amount = Integer.parseInt(args[0]);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < amount; i++) {
            Material material = MATERIALS[random.nextInt(MATERIALS.length)];
            sender.getInventory().addItem(new ItemStack(material, random.nextInt(1, material.getMaxStackSize() + 1)));
        }
        FunctionUtils.sendMessage(sender, "&eGiven random items.", true);
        return true;
    }
}