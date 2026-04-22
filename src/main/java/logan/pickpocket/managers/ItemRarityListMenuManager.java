package logan.pickpocket.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import logan.api.gui.MenuItem;
import logan.api.gui.PlayerInventoryMenu;
import logan.pickpocket.config.ItemRarityConfig;
import logan.pickpocket.config.ItemRarityConfig.RarityEntry;
import logan.pickpocket.config.ItemRarityConfig.RarityTier;

/**
 * Opens a paginated menu that lists all configured item rarities.
 */
public final class ItemRarityListMenuManager {

    private static final String MENU_TITLE = ChatColor.DARK_GREEN + "Item Rarity List";
    private static final int MENU_ROWS = 6;
    private static final int CONTENT_SLOTS = 45;
    private static final int PREVIOUS_SLOT = 45;
    private static final int PAGE_SLOT = 49;
    private static final int SORT_SLOT = 50;
    private static final int NEXT_SLOT = 53;

    private ItemRarityListMenuManager() {
    }

    public static void openFor(Player player) {
        openFor(player, 0, SortMode.A_Z);
    }

    public static void openFor(Player player, int page) {
        openFor(player, page, SortMode.A_Z);
    }

    public static void openFor(Player player, int page, SortMode sortMode) {
        List<RarityEntry> entries = getSortedEntries(sortMode);
        int totalPages = Math.max(1, (int) Math.ceil(entries.size() / (double) CONTENT_SLOTS));
        int clampedPage = Math.max(0, Math.min(page, totalPages - 1));
        int startIndex = clampedPage * CONTENT_SLOTS;
        int endIndex = Math.min(startIndex + CONTENT_SLOTS, entries.size());

        PlayerInventoryMenu menu = new PlayerInventoryMenu(MENU_TITLE, MENU_ROWS);
        fillBackground(menu);

        if (entries.isEmpty()) {
            menu.addItem(22, new MenuItem("No rarity items found", new ItemStack(Material.BARRIER))
                    .setLore(ChatColor.GRAY + "Check item_rarities.yml for valid entries."));
        } else {
            int slot = 0;
            for (int index = startIndex; index < endIndex; index++) {
                menu.addItem(slot++, createEntryItem(entries.get(index)));
            }
        }

        addNavigation(menu, player, clampedPage, totalPages, sortMode);
        menu.show(player);
    }

    private static void fillBackground(PlayerInventoryMenu menu) {
        for (int slot = CONTENT_SLOTS; slot < menu.getSize(); slot++) {
            menu.addItem(slot, new MenuItem(" ", new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
        }
    }

    private static MenuItem createEntryItem(RarityEntry entry) {
        RarityTier tier = ItemRarityConfig.resolveTier(entry.getValue());
        return new MenuItem(formatItemName(entry.getYamlKey()), new ItemStack(entry.getMaterial()))
                .setLore(
                        ChatColor.GRAY + "Tier: " + tier.getColor() + tier.getDisplayName(),
                        ChatColor.GRAY + "Value: " + ChatColor.WHITE
                                + String.format(Locale.US, "%.2f", entry.getValue()));
    }

    private static List<RarityEntry> getSortedEntries(SortMode sortMode) {
        List<RarityEntry> entries = new ArrayList<>(ItemRarityConfig.getEntries());
        Comparator<RarityEntry> byKeyAsc = Comparator.comparing(RarityEntry::getYamlKey);
        Comparator<RarityEntry> comparator = switch (sortMode) {
            case A_Z -> byKeyAsc;
            case Z_A -> byKeyAsc.reversed();
            case RARITY_HIGH -> Comparator.comparingDouble(RarityEntry::getValue)
                    .reversed()
                    .thenComparing(byKeyAsc);
            case RARITY_LOW -> Comparator.comparingDouble(RarityEntry::getValue)
                    .thenComparing(byKeyAsc);
        };
        entries.sort(comparator);
        return entries;
    }

    private static void addNavigation(PlayerInventoryMenu menu, Player player, int page, int totalPages, SortMode sortMode) {
        boolean hasPrevious = page > 0;
        boolean hasNext = page < totalPages - 1;

        menu.addItem(PREVIOUS_SLOT, hasPrevious
                ? new MenuItem("Previous Page", new ItemStack(Material.ARROW))
                        .setLore(ChatColor.GRAY + "Go to page " + ChatColor.WHITE + page)
                        .addListener(event -> openFor(player, page - 1, sortMode))
                : new MenuItem("Previous Page", new ItemStack(Material.BARRIER))
                        .setLore(ChatColor.GRAY + "No previous page."));

        menu.addItem(PAGE_SLOT, new MenuItem("Page " + (page + 1) + " / " + totalPages, new ItemStack(Material.BOOK))
                .setLore(ChatColor.GRAY + "Browse all configured rarity items."));

        menu.addItem(SORT_SLOT, new MenuItem("Sort: " + sortMode.getDisplayName(), new ItemStack(Material.HOPPER))
                .setLore(
                        ChatColor.GRAY + "Current: " + ChatColor.WHITE + sortMode.getDisplayName(),
                        ChatColor.GRAY + "Click to switch to " + ChatColor.WHITE + sortMode.next().getDisplayName())
                .addListener(event -> openFor(player, 0, sortMode.next())));

        menu.addItem(NEXT_SLOT, hasNext
                ? new MenuItem("Next Page", new ItemStack(Material.ARROW))
                        .setLore(ChatColor.GRAY + "Go to page " + ChatColor.WHITE + (page + 2))
                        .addListener(event -> openFor(player, page + 1, sortMode))
                : new MenuItem("Next Page", new ItemStack(Material.BARRIER))
                        .setLore(ChatColor.GRAY + "No next page."));
    }

    private static String formatItemName(String key) {
        String[] parts = key.split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    public enum SortMode {
        A_Z("A-Z"),
        Z_A("Z-A"),
        RARITY_HIGH("Rarity (high)"),
        RARITY_LOW("Rarity (low)");

        private static final SortMode[] VALUES = values();

        private final String displayName;

        SortMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SortMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }
}
