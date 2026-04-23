package logan.pickpocket.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.pickpocket.config.Config;

/**
 * Opens the admin-facing pickpocket configuration menu.
 */
public final class PickpocketConfigMenuManager {

    private static final String MENU_TITLE = ChatColor.DARK_GREEN + "Pickpocket Config";

    private PickpocketConfigMenuManager() {
    }

    public static void openFor(Player player) {
        PlayerInventoryMenu menu = new PlayerInventoryMenu(MENU_TITLE, 3);
        fillBackground(menu);
        menu.addItem(11, createTogglePickpocketingItem(player));
        menu.addItem(15, createReloadConfigItem(player));
        menu.show(player);
    }

    private static void fillBackground(PlayerInventoryMenu menu) {
        for (int slot = 0; slot < menu.getSize(); slot++) {
            menu.addItem(slot, new MenuItem(" ", new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
        }
    }

    private static MenuItem createTogglePickpocketingItem(Player player) {
        boolean enabled = Config.isPickpocketingEnabled();
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String state = enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
        return new MenuItem("Toggle Pickpocketing", new ItemStack(material))
                .setLore(
                        ChatColor.GRAY + "Current state: " + state,
                        ChatColor.GRAY + "Click to toggle server-wide",
                        ChatColor.GRAY + "pickpocketing availability.")
                .addListener(event -> {
                    boolean next = !Config.isPickpocketingEnabled();
                    Config.setPickpocketingEnabled(next);
                    Config.save();
                    player.sendMessage(next
                            ? ChatColor.GREEN + "Pickpocketing enabled."
                            : ChatColor.RED + "Pickpocketing disabled.");
                    openFor(player);
                });
    }

    private static MenuItem createReloadConfigItem(Player player) {
        return new MenuItem("Reload Config", new ItemStack(Material.PAPER))
                .setLore(
                        ChatColor.GRAY + "Reload config values from disk.",
                        ChatColor.GRAY + "Useful after manual file edits.")
                .addListener(event -> {
                    Config.reload();
                    player.sendMessage(ChatColor.GREEN + "Reloaded pickpocket config.");
                    openFor(player);
                });
    }
}
