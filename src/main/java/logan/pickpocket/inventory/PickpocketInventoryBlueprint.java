package logan.pickpocket.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Layout rules and slot typing for the pickpocket inventory blueprint editor.
 */
public final class PickpocketInventoryBlueprint {

    public static final int SIZE = 54;
    public static final int ROWS = 6;
    public static final int COLS = 9;
    public static final int MIN_USABLE_STEALABLE_SLOTS = 1;
    public static final int MAX_STEALABLE_SLOTS_IN_DEFAULT_LAYOUT = 27;

    public enum SlotKind {
        EMPTY,
        STEALABLE,
        HINT,
        TRAP_ITEM
    }

    private PickpocketInventoryBlueprint() {
    }

    /**
     * @param stack slot contents (may be null)
     * @return semantic kind for validation and board init
     */
    public static SlotKind kindFromItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return SlotKind.EMPTY;
        }
        return switch (stack.getType()) {
            case RED_STAINED_GLASS_PANE -> SlotKind.EMPTY;
            case GREEN_STAINED_GLASS_PANE -> SlotKind.STEALABLE;
            case BLUE_STAINED_GLASS_PANE -> SlotKind.HINT;
            default -> SlotKind.TRAP_ITEM;
        };
    }

    /**
     * Normalizes editor contents for persistence: air or null becomes red glass.
     */
    public static ItemStack normalizeSlot(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return new ItemStack(Material.RED_STAINED_GLASS_PANE);
        }
        return stack.clone();
    }

    /**
     * @param contents blueprint length {@link #SIZE}
     * @return number of {@link SlotKind#TRAP_ITEM} slots
     */
    public static int countTrapItemSlots(ItemStack[] contents) {
        int n = 0;
        for (ItemStack stack : contents) {
            if (kindFromItem(stack) == SlotKind.TRAP_ITEM) {
                n++;
            }
        }
        return n;
    }

    /**
     * Validates adjacency and minimum pure green count.
     *
     * @param contents    54 slots
     * @param minPureGreens required {@link SlotKind#STEALABLE} count
     * @return null if valid, otherwise a short English reason for messaging
     */
    public static String validate(ItemStack[] contents, int minPureGreens) {
        if (contents == null || contents.length != SIZE) {
            return "layout must be 54 slots.";
        }
        int pureGreens = 0;
        for (ItemStack stack : contents) {
            if (kindFromItem(stack) == SlotKind.STEALABLE) {
                pureGreens++;
            }
        }
        if (pureGreens < minPureGreens) {
            return "need at least " + minPureGreens + " green panes (have " + pureGreens + ").";
        }
        for (int slot = 0; slot < SIZE; slot++) {
            SlotKind kind = kindFromItem(contents[slot]);
            if (kind == SlotKind.STEALABLE && !touchesKind(contents, slot, SlotKind.HINT)) {
                return "each green pane must touch a blue pane.";
            }
            if (kind == SlotKind.HINT && !touchesKind(contents, slot, SlotKind.STEALABLE)) {
                return "each blue pane must touch a green pane.";
            }
            if (kind == SlotKind.TRAP_ITEM && !touchesKind(contents, slot, SlotKind.HINT)) {
                return "each trap must touch a blue pane.";
            }
        }
        return null;
    }

    /**
     * Clamps requested green-slot minimums into the range this layout model supports.
     */
    public static int normalizeRequiredStealableSlots(int requestedMinimum) {
        return Math.max(
                MIN_USABLE_STEALABLE_SLOTS,
                Math.min(MAX_STEALABLE_SLOTS_IN_DEFAULT_LAYOUT, requestedMinimum));
    }

    /**
     * Builds a deterministic checkerboard baseline that satisfies all adjacency rules.
     */
    public static ItemStack[] createDeterministicValidLayout(int requiredStealableSlots) {
        int targetStealableSlots = normalizeRequiredStealableSlots(requiredStealableSlots);
        ItemStack[] contents = new ItemStack[SIZE];
        for (int slot = 0; slot < SIZE; slot++) {
            int row = slot / COLS;
            int col = slot % COLS;
            boolean even = ((row + col) % 2) == 0;
            Material material = even ? Material.BLUE_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            contents[slot] = new ItemStack(material);
        }
        int assignedStealables = 27;
        if (assignedStealables < targetStealableSlots) {
            return createMinimalUsableLayout();
        }
        return contents;
    }

    /**
     * Minimal guaranteed-usable baseline: one green touching one blue.
     */
    public static ItemStack[] createMinimalUsableLayout() {
        ItemStack[] contents = new ItemStack[SIZE];
        for (int slot = 0; slot < SIZE; slot++) {
            contents[slot] = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        }
        int greenSlot = (2 * COLS) + 3;
        int blueSlot = greenSlot + 1;
        contents[greenSlot] = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        contents[blueSlot] = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        return contents;
    }

    private static boolean touchesKind(ItemStack[] contents, int slot, SlotKind requiredNeighbor) {
        int row = slot / COLS;
        int col = slot % COLS;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int nr = row + dr;
                int nc = col + dc;
                if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) {
                    continue;
                }
                int nSlot = nr * COLS + nc;
                if (kindFromItem(contents[nSlot]) == requiredNeighbor) {
                    return true;
                }
            }
        }
        return false;
    }
}
