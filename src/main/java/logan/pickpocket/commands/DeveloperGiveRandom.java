package logan.pickpocket.commands;

import logan.api.command.BasicCommand;
import logan.api.command.SenderTarget;
import logan.api.util.FunctionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class DeveloperGiveRandom extends BasicCommand<Player> {

    public DeveloperGiveRandom() {
        super("giverandom", "pickpocket.developer.giverandom", 1, 1,
                new String[]{"gr"}, SenderTarget.PLAYER, "developer",
                List.of(String.class),
                "/pickpocket developer giverandom <amount>");
    }

    @Override
    public boolean run(Player sender, String[] args, Object data) {
        int maxMaterials = Material.values().length;
        int amount = Integer.parseInt(args[0]);
        Random random = new Random();
        for (int i = 0; i <= amount; i++) {
            Material material = Material.values()[random.nextInt(maxMaterials - 1)];
            sender.getInventory().addItem(new ItemStack(material, (int) (Math.random() * material.getMaxStackSize() - 1)));
        }
        FunctionUtils.sendMessage(sender, "&eGiven random items.", true);
        return true;
    }
}
