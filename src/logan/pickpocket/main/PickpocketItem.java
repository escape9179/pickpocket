package logan.pickpocket.main;

import com.google.common.collect.Lists;
import logan.pickpocket.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Tre on 12/24/2015.
 */
@SuppressWarnings("deprecation")
public enum PickpocketItem {

    /*

    How to calculate experience:
    Formula: (1 - chance) * 100

    Equation to calculate chance based on player experience:
    ((playersExperience / (itemExperience / itemChance)) / 100) + itemChance

     */

    STONE("Stone", new ItemStack(1), 2),
    GRASS("Grass", new ItemStack(2), 2),
    DIRT("Dirt", new ItemStack(3), 1),
    COBBLESTONE("Cobblestone", new ItemStack(4), 1),
    OAK_PLANK("Oak Plank", new ItemStack(5), 2),
    BIRCH_PLANK("Birch Plank", new ItemStack(5, 1, (short) 2), 2),
    JUNGLE_PLANK("Jungle Plank", new ItemStack(5, 1, (short) 3), 2),
    OAK_SAPLING("Oak Sapling", new ItemStack(6), 1),
    SPRUCE_SAPLING("Spruce Sapling", new ItemStack(6, 1, (short) 1), 1),
    BIRCH_SAPLING("Birch Sapling", new ItemStack(6, 1, (short) 2), 1),
    JUNGLE_SAPLING("Jungle Sapling", new ItemStack(6, 1, (short) 3), 1),
    BEDROCK("Bedrock", new ItemStack(7), 0),
    SAND("Sand", new ItemStack(12), 1),
    GRAVEL("Gravel", new ItemStack(13), 1),
    GOLD_ORE("Gold Ore", new ItemStack(14), 1000),
    IRON_ORE("Iron Ore", new ItemStack(15), 300),
    COAL_ORE("Coal Ore", new ItemStack(16), 140),
    OAK_WOOD("Oak Wood", new ItemStack(17), 8),
    SPRUCE_WOOD("Spruce Wood", new ItemStack(17, 1, (short) 1), 8),
    BIRCH_WOOD("Birch Wood", new ItemStack(17, 1, (short) 2), 8),
    JUNGLE_WOOD("Jungle Wood", new ItemStack(17, 1, (short) 3), 8),
    OAK_LEAVES("Oak Leaves", new ItemStack(18), 0.4),
    SPRUCE_LEAVES("Spruce Leaves", new ItemStack(18, 1, (short) 1), 0.40),
    BIRCH_LEAVES("Birch Leaves", new ItemStack(18, 1, (short) 2), 0.40),
    JUNGLE_LEAVES("Jungle Leaves", new ItemStack(18, 1, (short) 3), 0.40),
    SPONGE("Sponge", new ItemStack(19), 100_000),
    GLASS("Glass", new ItemStack(20), 17),
    LAPIS_LAZULI_ORE("Lapis Lazuli Ore", new ItemStack(21), 300),
    LAPIS_LAZULI_BLOCK("Lapis Lazuli Block", new ItemStack(22), 385.71),
    DISPENSER("Dispenser", new ItemStack(23), 70),
    SANDSTONE("Sandstone", new ItemStack(24), 4),
    CHISELED_SANDSTONE("Chiseled Sandstone", new ItemStack(24, 1, (short) 1), 4),
    SMOOTH_SANDSTONE("Smooth Sandstone", new ItemStack(24, 1, (short) 2), 4),
    NOTE_BLOCK("Note Block", new ItemStack(25), 46),
    POWERED_RAIL("Powered Rail", new ItemStack(27), 905.17),
    DEETECTOR_RAIL("Detector Rail", new ItemStack(28), 275.67),
    STICKY_PISTON("Sticky Piston", new ItemStack(29), 314),
    WEB("Web", new ItemStack(30), 100),
    DEAD_SHRUB("Dead Shrub", new ItemStack(31), 4),
    TALL_GRASS("Tall Grass", new ItemStack(31, 1, (short) 1), 4),
    FERN("Fern", new ItemStack(31, 1, (short) 2), 4),
    //Another dead shrub?
    PISTON("Piston", new ItemStack(33), 310),
    WOOL("Wool", new ItemStack(35), 40),
    ORANGE_WOOL("Orange Wool", new ItemStack(35, 1, (short) 1), 49),
    MAGENTA_WOOL("Magenta Wool", new ItemStack(35, 1, (short) 2), 56.38),
    LIGHT_BLUE_WOOL("Light Blue Wool", new ItemStack(35, 1, (short) 3), 62.76),
    YELLOW_WOOL("Yellow Wool", new ItemStack(35, 1, (short) 4), 48),
    LIME_WOOL("Lime Wool", new ItemStack(35, 1, (short) 5), 59.33),
    PINK_WOOL("Pink Wool", new ItemStack(35, 1, (short) 6), 46.33),
    GRAY_WOOL("Gray Wool", new ItemStack(35, 1, (short) 7), 51.33),
    LIGHT_GRAY_WOOL("Light Gray Wool", new ItemStack(35, 1, (short) 8), 48.44),
    CYAN_WOOL("Cyan Wool", new ItemStack(35, 1, (short) 9), 79.43),
    PURPLE_WOOL("Purple Wool", new ItemStack(35, 1, (short) 10), 66.43),
    BLUE_WOOL("Blue Wool", new ItemStack(35, 1, (short) 11), 82.86),
    BROWN_WOOL("Brown Wool", new ItemStack(35, 1, (short) 12), 44),
    GREEN_WOOL("Green Wool", new ItemStack(35, 1, (short) 13), 76),
    RED_WOOL("Red Wool", new ItemStack(35, 1, (short) 14), 50),
    BLACK_WOOL("Black Wool", new ItemStack(35, 1, (short) 15), 60),
    DANDELION("Dandelion", new ItemStack(37), 16),
    ROSE("Rose", new ItemStack(38), 20),
    BROWN_MUSHROOM("Brown Mushroom", new ItemStack(39), 16),
    RED_MUSHROOM("Red Mushroom", new ItemStack(40), 20),
    GOLD_BLOCK("Gold Block", new ItemStack(41), 8100),
    IRON_BLOCK("Iron Block", new ItemStack(42), 2430),
    STONE_SLAB("Stone Slab", new ItemStack(44), 1),
    SANDSTONE_SLAB("Sandstone Slab", new ItemStack(44, 1, (short) 1), 2),
    WOODEN_SLAB("Wooden Slab", new ItemStack(44, 1, (short) 2), 1),
    COBBLESTONE_SLAB("Cobblestone Slab", new ItemStack(44, 1, (short) 3), 0.50),
    BRICK_SLAB("Brick Slab", new ItemStack(44, 1, (short) 4), 52),
    STONE_BRICK_SLAB("Stone Brick Slab", new ItemStack(44, 1, (short) 5), 1),
    NETHER_BRICK_SLAB("Nether Brick Slab", new ItemStack(44, 1, (short) 6), 32.80),
    QUARTZ_SLAB("Quartz Slab", new ItemStack(44, 1, (short) 7), 140),
    BRICK("Brick", new ItemStack(45), 104);

    private String name;
    private String rawName;
    private Material material;
    private ItemStack lockedItemStack;
    private ItemStack itemStack;
    private ItemStack rawItemStack;
    private double chance;
    private double value;
    private double maxValue = 4_000_000;
    private DecimalFormat decimalFormat = new DecimalFormat("#.###%");

    PickpocketItem(String name, ItemStack itemStack, double value) {
        this.itemStack = itemStack;
        this.material = itemStack.getType();
        this.value = value;
        this.rawName = name;
        this.rawItemStack = new ItemStack(material);

        this.chance = convertToChance(value);

        if (value <= 20) this.name = ChatColor.DARK_GRAY + name;
        else if (value <= 40) this.name = ChatColor.GRAY + name;
        else if (value <= 60) this.name = ChatColor.DARK_RED + name;
        else if (value <= 80) this.name = ChatColor.RED + name;
        else if (value <= 100) this.name = ChatColor.GOLD + name;
        else if (value <= 140) this.name = ChatColor.YELLOW + name;
        else if (value <= 160) this.name = ChatColor.DARK_GREEN + name;
        else if (value <= 180) this.name = ChatColor.GREEN + name;
        else if (value <= 200) this.name = ChatColor.DARK_AQUA + name;
        else if (value <= 400) this.name = ChatColor.AQUA + name;
        else if (value <= 600) this.name = ChatColor.DARK_BLUE + name;
        else if (value <= 800) this.name = ChatColor.BLUE + name;
        else if (value <= 1000) this.name = ChatColor.LIGHT_PURPLE + name;
        else if (value <= 1500) this.name = ChatColor.DARK_PURPLE + name;
        else if (value <= 2000) this.name = ChatColor.WHITE + name;
        else this.name = ChatColor.GRAY + name;
    }

    public String getName() {
        return name;
    }

    public String getRawName() {
        return rawName;
    }


    public ItemStack getLockedItemStack(Profile profile) {
        lockedItemStack = new ItemStack(Material.IRON_FENCE);
        ItemMeta lockedItemStackItemMeta = lockedItemStack.getItemMeta();
        lockedItemStackItemMeta.setDisplayName(name + ChatColor.DARK_RED + " [LOCKED]");
        ArrayList lockedItemStackLoreList = Lists.<String>newArrayList();
        lockedItemStackLoreList.add(ChatColor.GRAY + "Chance: " + ChatColor.WHITE + decimalFormat.format(convertToChance(value)));

        lockedItemStackItemMeta.setLore(lockedItemStackLoreList);
        lockedItemStack.setItemMeta(lockedItemStackItemMeta);
        return lockedItemStack;
    }

    public ItemStack getItemStack(Profile profile) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        ArrayList<String> itemStackLore = new ArrayList<>();
        itemStackLore.add(ChatColor.GRAY + "Chance: " + ChatColor.WHITE + decimalFormat.format(convertToChance(value)));
        itemStackLore.add(ChatColor.GRAY + "Your chance: " + ChatColor.WHITE + decimalFormat.format(calculateStolenBasedChance(profile.getPickpocketItemModule().getStealsOf(this))));
        itemStackLore.add(ChatColor.GRAY + "Times Stolen: " + ChatColor.WHITE + NumberFormat.getInstance().format(profile.getPickpocketItemModule().getStealsOf(this)));
        itemMeta.setLore(itemStackLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getRawItemStack() {
        return rawItemStack;
    }

    public Material getMaterial() {
        return material;
    }

    public double calculateStolenBasedChance(double steals) {
        return (steals / 1000) + chance;
    }

    public static PickpocketItem getPickpocketItemByName(String rawName) {
        for (PickpocketItem pickpocketItem : values()) {
            if (rawName.equals(pickpocketItem.getRawName())) {
                return pickpocketItem;
            }
        }
        return null;
    }

    public double convertToChance(double value) {
        return (((maxValue - value) * 20) / maxValue) / 100;
    }
}
