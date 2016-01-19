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
public enum PickpocketItem {

    /*

    How to calculate experience:
    Formula: (1 - chance) * 100

    Equation to calculate chance based on player experience:
    ((playersExperience / (itemExperience / itemChance)) / 100) + itemChance

     */

    //TODO Finish overhauling items.
    POISONOUS_POTATO("Poisonous Potato", Material.POISONOUS_POTATO, 1),
    COOKIE("Cookie", Material.COOKIE, 1),
    DIRT("Dirt", Material.DIRT, 1),
    SAND("Sand", Material.SAND, 1),
    COBBLESTONE("Cobblestone", Material.COBBLESTONE, 1),
    NETHERRACK("Netherrack", Material.NETHERRACK, 1),
    ROTTEN_FLESH("Rotten Flesh", Material.ROTTEN_FLESH, 1),
    SNOW_BALL("Snowball", Material.SNOW_BALL, 2),
    STONE("Stone", Material.STONE, 2),
    MELON("Melon Slice", Material.MELON, 2),
    SEEDS("Seeds", Material.SEEDS, 2),
    MELON_SEEDS("Melon Seeds", Material.MELON_SEEDS, 2),
    COCOA("Cocoa Beans", Material.COCOA, 2),
    WHEAT("Wheat", Material.WHEAT, 2),
    SUGAR_CANE("Sugar Cane", Material.SUGAR_CANE, 2),
    SUGAR("Sugar", Material.SUGAR, 2),
    EGG("Egg", Material.EGG, 2),
    CLAY_BALL("Clay", Material.CLAY_BALL, 3),
    GRAVEL("Gravel", Material.GRAVEL, 3),
    PUMPKIN_SEEDS("Pumpkin Seeds", Material.PUMPKIN_SEEDS, 3),
    GLASS_BOTTLE("Glass Bottle", Material.GLASS_BOTTLE, 3),
    ARROW("Arrow", Material.ARROW, 3),
    GRASS("Grass", Material.GRASS, 4),
    REDSTONE("Redstone Dust", Material.REDSTONE, 4),
    SNOW_BLOCK("Snow Block", Material.SNOW_BLOCK, 4),
    GLOWSTONE_DUST("Glowstone Dust", Material.GLOWSTONE_DUST, 4),
    POTATO_ITEM("Raw Potato", Material.POTATO_ITEM, 4),
    SOUL_SAND("Sould sand", Material.SOUL_SAND, 4),
    CARROT_ITEM("Carrot", Material.CARROT_ITEM, 4),
    FLINT("Flint", Material.FLINT, 4),
    FEATHER("Feather", Material.FEATHER, 4),
    STRING("String", Material.STRING, 4),
    NETHER_WARTS("Nether Wart", Material.NETHER_WARTS, 4),
    MOSSY_COBBLESTONE("Mossy Cobble", Material.MOSSY_COBBLESTONE, 5),
    BAKED_POTATO("Baked Potato", Material.BAKED_POTATO, 5),
    BREAD("Bread", Material.BREAD, 5),
    GOLD_NUGGET("Gold Nugget", Material.GOLD_NUGGET, 5),
    ICE("Ice", Material.ICE, 6),
    WOOL("Wool", Material.WOOL, 6),
    RED_MUSHROOM("Red Mushroom", Material.RED_MUSHROOM, 6),
    BONE("Bone", Material.BONE, 6),
    COAL("Coal", Material.COAL, 8),
    BROWN_MUSHROOM("Brown Mushroom", Material.BROWN_MUSHROOM, 6),
    RAW_CHICKEN("Raw Chicken", Material.RAW_CHICKEN, 8),
    SPIDER_EYE("Spider Eye", Material.SPIDER_EYE, 8),
    COOKED_CHICKEN("Cooked Chicken", Material.COOKED_CHICKEN, 9),
    QUARTZ("Nether Quartz", Material.QUARTZ, 10),
    MYCEL("Mycelium", Material.MYCEL, 10),
    RAW_BEEF("Raw Beef", Material.RAW_BEEF, 10),
    PUMPKIN("Pumpkin", Material.PUMPKIN, 10),
    SULPHUR("Gunpowder", Material.SULPHUR, 10),
    ENDER_PEARL("Ender Pearl", Material.ENDER_PEARL, 10),
    FISHING_ROD("Fishing Rod", Material.FISHING_ROD, 10),
    COOKED_BEEF("Cooked Beef", Material.COOKED_BEEF, 11),
    IRON_ORE("Iron Ore", Material.IRON_ORE, 12),
    CLAY("Clay Block", Material.CLAY, 12),
    OBSIDIAN("Obsidian", Material.OBSIDIAN, 12),
    PORK("Raw Porkchop", Material.PORK, 12),
    GRILLED_PORK("Grilled Porkchop", Material.GRILLED_PORK, 13),
    GLOWSTONE("Glowstone Block", Material.GLOWSTONE, 15),
    IRON_INGOT("Iron Ingot", Material.IRON_INGOT, 15),
    MUSHROOM_SOUP("Mushroom Stew", Material.MUSHROOM_SOUP, 15),
    PUMPKIN_PIE("Pumpkin Pie", Material.PUMPKIN_PIE, 15),
    WOOD_SWORD("Wooden Sword", Material.WOOD_SWORD, 15),
    WOOD_SPADE("Wooden Shovel", Material.WOOD_SPADE, 15),
    STONE_SWORD("Stone Sword", Material.STONE_SWORD, 15),
    STONE_SPADE("Stone Shovel", Material.STONE_SPADE, 15),
    FERMENTED_SPIDER_EYE("Fermented Spider Eye", Material.FERMENTED_SPIDER_EYE, 18),
    ENDSTONE("Endstone", Material.ENDER_STONE, 20),
    RAW_FISH("Raw Fish", Material.RAW_FISH, 20),
    HAY_BLOCK("Hay Bale", Material.HAY_BLOCK, 20),
    LEATHER("Leather", Material.LEATHER, 20),
    BOW("Bow", Material.BOW, 20),
    FLINT_AND_STEEL("Flint and Steel", Material.FLINT_AND_STEEL, 20),
    COOKED_FISH("Cooked Fish", Material.COOKED_FISH, 21),
    MELON_BLOCK("Melon Block", Material.MELON_BLOCK, 25),
    SLIME_BALL("Slimeball", Material.SLIME_BALL, 25),
    BLAZE_POWDER("Blaze Poweder", Material.BLAZE_POWDER, 25),
    IRON_SPADE("Iron Shovel", Material.IRON_SPADE, 25),
    WOOD_HOE("Wood Hoe", Material.WOOD_HOE, 25),
    STONE_HOE("Stone Hoe", Material.STONE_HOE, 25),
    COAL_ORE("Coal Ore", Material.COAL_ORE, 30),
    GOLD_ORE("Gold Ore", Material.GOLD_ORE, 30),
    CAKE("Cake", Material.CAKE, 30),
    SHEARS("Shears", Material.SHEARS, 30),
    WOOD_PICKAXE("Wooden Pickaxe", Material.WOOD_PICKAXE, 30),
    WOOD_AXE("Wooden Axe", Material.WOOD_AXE, 30),
    STONE_PICKAXE("Stone Pickaxe", Material.STONE_PICKAXE, 30),
    STONE_AXE("Stone Axe", Material.STONE_AXE, 30),
    EYE_OF_ENDER("Eye of Ender", Material.EYE_OF_ENDER, 35),
    REDSTONE_BLOCK("Redstone Block", Material.REDSTONE_BLOCK, 36),
    GOLD_INGOT("Gold Ingot", Material.GOLD_INGOT, 40),
    QUARTZ_ORE("Nether Quartz Ore", Material.QUARTZ_ORE, 40),
    QUARTZ_BLOCK("Nether Quartz Block", Material.QUARTZ_BLOCK, 40),
    BUCKET("Bucket", Material.BUCKET, 40),
    SPECKLED_MELON("Glistering Melon", Material.SPECKLED_MELON, 40),
    IRON_SWORD("Iron Sword", Material.IRON_SWORD, 40),
    MILK("Milk", Material.MILK_BUCKET, 45),
    WATER_BUCKET("Water Bucket", Material.WATER_BUCKET, 45),
    LAVA_BUCKET("Lava Bucket", Material.LAVA_BUCKET, 45),
    BLAZE_ROD("Blaze Rod", Material.BLAZE_ROD, 50),
    MAGMA_CREAM("Magma Cream", Material.MAGMA_CREAM, 50),
    IRON_HOE("Iron Hoe", Material.IRON_HOE, 50),
    BREWING_STAND_ITEM("Brewing Stand", Material.BREWING_STAND_ITEM, 55),
    REDSTONE_ORE("Redstone Ore", Material.REDSTONE_ORE, 60),
    APPLE("Apple", Material.APPLE, 60),
    GOLD_SPADE("Golden Shovel", Material.GOLD_SPADE, 60),
    COMPASS("Compass", Material.COMPASS, 65),
    IRON_PICKAXE("Iron Pickaxe", Material.IRON_PICKAXE, 65),
    IRON_AXE("Iron Axe", Material.IRON_AXE, 65),
    LAPIS_BLOCK("Lapis Lazuli Block", Material.LAPIS_BLOCK, 70),
    COAL_BLOCK("Coal Block", Material.COAL_BLOCK, 72),
    GOLD_SWORD("Gold Sword", Material.GOLD_SWORD, 90),
    EMERALD("Emerald", Material.EMERALD, 100),
    GOLD_HOE("Golden Hoe", Material.GOLD_HOE, 100),
    LAPIS_ORE("Lapis Lazuli Ore", Material.LAPIS_ORE, 120),
    IRON_BLOCK("Iron Block", Material.IRON_BLOCK, 135),
    DIAMOND("Diamond", Material.DIAMOND, 160),
    CLOCK("Clock", Material.WATCH, 165),
    DIAMOND_SPADE("Diamond Shovel", Material.DIAMOND_SPADE, 180),
    GHAST_TEAR("Ghast Tear", Material.GHAST_TEAR, 200),
    SEA_LANTERN("Sea Lantern", Material.SEA_LANTERN, 200),
    DIAMOND_SWORD("Diamond Sword", Material.DIAMOND_SWORD, 330),
    DIAMOND_HOE("Diamond Hoe", Material.DIAMOND_HOE, 340),
    GOLD_BLOCK("Gold Block", Material.GOLD_BLOCK, 360),
    GOLDEN_APPLE("Golden Apple", Material.GOLDEN_APPLE, 400),
    DIAMOND_PICKAXE("Diamond Pickaxe", Material.DIAMOND_PICKAXE, 500),
    DIAMOND_AXE("Diamond Axe", Material.DIAMOND_AXE, 500),
    DIAMOND_ORE("Diamond Ore", Material.DIAMOND_ORE, 640),
    EMERALD_ORE("Emerald Ore", Material.EMERALD_ORE, 800),
    EMERALD_BLOCK("Emerald Block", Material.EMERALD_BLOCK, 900),
    DIAMOND_BLOCK("Diamond Block", Material.DIAMOND_BLOCK, 1450);

    private String name;
    private String rawName;
    private Material material;
    private ItemStack lockedItemStack;
    private ItemStack itemStack;
    private ItemStack rawItemStack;
    private double chance;
    private int value;
    private int maxValue = 2000;
    private DecimalFormat decimalFormat = new DecimalFormat("#.###%");

    PickpocketItem(String name, Material material, int value) {
        this.material = material;
        this.value = value;
        this.rawName = name;
        this.rawItemStack = new ItemStack(material);

        this.chance = ((maxValue - value) * .05) / 1000;

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
        lockedItemStack = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta lockedItemStackItemMeta = lockedItemStack.getItemMeta();
        lockedItemStackItemMeta.setDisplayName(name);
        ArrayList lockedItemStackLoreList = Lists.<String>newArrayList();
        lockedItemStackLoreList.add(ChatColor.GRAY + "Chance: " + ChatColor.WHITE + decimalFormat.format(chance));

        lockedItemStackItemMeta.setLore(lockedItemStackLoreList);
        lockedItemStack.setItemMeta(lockedItemStackItemMeta);
        return lockedItemStack;
    }

    public ItemStack getItemStack(Profile profile) {
        itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        ArrayList<String> itemStackLore = new ArrayList<>();
        itemMeta.setLore(getLockedItemStack(profile).getItemMeta().getLore());
        itemStackLore.add(ChatColor.GRAY + "Your chance: " + ChatColor.WHITE + decimalFormat.format(calculateStolenBasedChance(profile.getPickpocketItemModule().getStealsOf(this))));
        itemStackLore.add(ChatColor.GRAY + "Times Stolen: " + ChatColor.WHITE + NumberFormat.getInstance().format(profile.getPickpocketItemModule().getStealsOf(this)));
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
}
