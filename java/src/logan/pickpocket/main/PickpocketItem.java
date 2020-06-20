package logan.pickpocket.main;

import com.google.common.collect.Lists;
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



    POISONOUS_POTATO("Poisonous Potato", "Steal a poisonous potato.", Material.POISONOUS_POTATO, 1),
    COOKIE("Cookie", "Steal a cookie.", Material.COOKIE, 1),
    DIRT("Dirt", "Steal dirt.", Material.DIRT, 1),
    SAND("Sand", "Steal sand.", Material.SAND, 1),
    COBBLESTONE("Cobblestone", "Steal cobblestone", Material.COBBLESTONE, 1),
    NETHERRACK("Netherrack", "Steal netherrack.", Material.NETHERRACK, 1),
    ROTTEN_FLESH("Rotten Flesh", "Steal rotten flesh.", Material.ROTTEN_FLESH, 1),
    SNOW_BALL("Snowball", "Steal a snowball.", Material.SNOW_BALL, 2),
    STONE("Stone", "Steal stone.", Material.STONE, 2),
    MELON("Melon Slice", "Steal a melon slice.", Material.MELON, 2),
    SEEDS("Seeds", "Steal seeds.", Material.SEEDS, 2),
    MELON_SEEDS("Melon Seeds", "Steal melon seeds.", Material.MELON_SEEDS, 2),
    COCOA("Cocoa Beans", "Steal cocoa beans.", Material.COCOA, 2),
    WHEAT("Wheat", "Steal wheat.", Material.WHEAT, 2),
    SUGAR_CANE("Sugar Cane", "Steal sugar cane.", Material.SUGAR_CANE, 2),
    SUGAR("Sugar", "Steal sugar.", Material.SUGAR, 2),
    EGG("Egg", "Steal an egg.", Material.EGG, 2),
    CLAY_BALL("Clay", "Steal clay.", Material.CLAY_BALL, 3),
    GRAVEL("Gravel", "Steal gravel.", Material.GRAVEL, 3),
    PUMPKIN_SEEDS("Pumpkin Seeds", "Steal pumpkin seeds.", Material.PUMPKIN_SEEDS, 3),
    GLASS_BOTTLE("Glass Bottle", "Steal a glass bottle.", Material.GLASS_BOTTLE, 3),
    ARROW("Arrow", "Steal an arrow.", Material.ARROW, 3),
    GRASS("Grass", "Steal grass.", Material.GRASS, 4),
    REDSTONE("Redstone Dust", "Steal redstone dust.", Material.REDSTONE, 4),
    SNOW_BLOCK("Snow Block", "Steal a snow block.", Material.SNOW_BLOCK, 4),
    GLOWSTONE_DUST("Glowstone Dust", "Steal glowstone dust", Material.GLOWSTONE_DUST, 4),
    POTATO_ITEM("Raw Potato", "Steal a raw potato.", Material.POTATO_ITEM, 4),
    SOUL_SAND("Sould sand", "Steal soul sand.", Material.SOUL_SAND, 4),
    CARROT_ITEM("Carrot", "Steal a carrot.", Material.CARROT_ITEM, 4),
    FLINT("Flint", "Steal flint.", Material.FLINT, 4),
    FEATHER("Feather", "Steal a feather.", Material.FEATHER, 4),
    STRING("String", "Steal string.", Material.STRING, 4),
    NETHER_WARTS("Nether Wart", "Steal nether warts.", Material.NETHER_WARTS, 4),
    MOSSY_COBBLESTONE("Mossy Cobble", "Steal mossy cobblestone.", Material.MOSSY_COBBLESTONE, 5),
    BAKED_POTATO("Baked Potato", "Steal a baked potato.", Material.BAKED_POTATO, 5),
    BREAD("Bread", "Steal bread.", Material.BREAD, 5),
    GOLD_NUGGET("Gold Nugget", "Steal a gold nugget.", Material.GOLD_NUGGET, 5),
    ICE("Ice", "Steal ice.", Material.ICE, 6),
    WOOL("Wool", "Steal wool.", Material.WOOL, 6),
    RED_MUSHROOM("Red Mushroom", "Steal a red mushroom.", Material.RED_MUSHROOM, 6),
    BONE("Bone", "Steal a bone.", Material.BONE, 6),
    COAL("Coal", "Steal coal.", Material.COAL, 8),
    BROWN_MUSHROOM("Brown Mushroom", "Steal a brow mushroom.", Material.BROWN_MUSHROOM, 6),
    RAW_CHICKEN("Raw Chicken", "Steal chicken.", Material.RAW_CHICKEN, 8),
    SPIDER_EYE("Spider Eye", "Steal a spider eye.", Material.SPIDER_EYE, 8),
    COOKED_CHICKEN("Cooked Chicken", "Steal cooked chickcen.", Material.COOKED_CHICKEN, 9),
    QUARTZ("Nether Quartz", "Steal nether quartz.", Material.QUARTZ, 10),
    MYCEL("Mycelium", "Steal mycelium.", Material.MYCEL, 10),
    RAW_BEEF("Raw Beef", "Steal raw beef.", Material.RAW_BEEF, 10),
    PUMPKIN("Pumpkin", "Steal a pumpkin.", Material.PUMPKIN, 10),
    SULPHUR("Gunpowder", "Steal gunpowder.", Material.SULPHUR, 10),
    ENDER_PEARL("Ender Pearl", "Steal an ender pearl.", Material.ENDER_PEARL, 10),
    FISHING_ROD("Fishing Rod", "Steal a fishing rod.", Material.FISHING_ROD, 10),
    COOKED_BEEF("Cooked Beef", "Steal cooked beef.", Material.COOKED_BEEF, 11),
    IRON_ORE("Iron Ore", "Steal iron ore.", Material.IRON_ORE, 12),
    CLAY("Clay Block", "Steal a clay block.", Material.CLAY, 12),
    OBSIDIAN("Obsidian", "Steal obsidian.", Material.OBSIDIAN, 12),
    PORK("Raw Porkchop", "Steal raw porkchop.", Material.PORK, 12),
    GRILLED_PORK("Grilled Porkchop", "Steal a grilled porkchop.", Material.GRILLED_PORK, 13),
    GLOWSTONE("Glowstone Block", "Steal a glowstone block", Material.GLOWSTONE, 15),
    IRON_INGOT("Iron Ingot", "Steal an iron ingot.", Material.IRON_INGOT, 15),
    MUSHROOM_SOUP("Mushroom Stew", "Steal mushroom stew.", Material.MUSHROOM_SOUP, 15),
    PUMPKIN_PIE("Pumpkin Pie", "Steal a pumpkin pie.", Material.PUMPKIN_PIE, 15),
    WOOD_SWORD("Wooden Sword", "Steal a wooden sword.", Material.WOOD_SWORD, 15),
    WOOD_SPADE("Wooden Shovel", "Steal a wooden shovel.", Material.WOOD_SPADE, 15),
    STONE_SWORD("Stone Sword", "Steal a stone sword.", Material.STONE_SWORD, 15),
    STONE_SPADE("Stone Shovel", "Steal a stone shovel.", Material.STONE_SPADE, 15),
    FERMENTED_SPIDER_EYE("Fermented Spider Eye", "Steal a fermented spider eye", Material.FERMENTED_SPIDER_EYE, 18),
    ENDSTONE("Endstone", "Steal endstone.", Material.ENDER_STONE, 20),
    RAW_FISH("Raw Fish", "Steal raw fish.", Material.RAW_FISH, 20),
    HAY_BLOCK("Hay Bale", "Steal a hay bale.", Material.HAY_BLOCK, 20),
    LEATHER("Leather", "Steal leather.", Material.LEATHER, 20),
    BOW("Bow", "Steal a bow.", Material.BOW, 20),
    FLINT_AND_STEEL("Flint and Steel", "Steal flint and steel.", Material.FLINT_AND_STEEL, 20),
    COOKED_FISH("Cooked Fish", "Steal cooked fish.", Material.COOKED_FISH, 21),
    MELON_BLOCK("Melon Block", "Steal a melon block.", Material.MELON_BLOCK, 25),
    SLIME_BALL("Slimeball", "Steal a slimeball.", Material.SLIME_BALL, 25),
    BLAZE_POWDER("Blaze Poweder", "Steal blaze powder.", Material.BLAZE_POWDER, 25),
    IRON_SPADE("Iron Shovel", "Steal an iron shovel.", Material.IRON_SPADE, 25),
    WOOD_HOE("Wood Hoe", "Steal a wooden hoe.", Material.WOOD_HOE, 25),
    STONE_HOE("Stone Hoe", "Steal a stone hoe.", Material.STONE_HOE, 25),
    COAL_ORE("Coal Ore", "Steal coal ore.", Material.COAL_ORE, 30),
    GOLD_ORE("Gold Ore", "Steal gold ore.", Material.GOLD_ORE, 30),
    CAKE("Cake", "Steal a cake.", Material.CAKE, 30),
    SHEARS("Shears", "Steal a pair of shears.", Material.SHEARS, 30),
    WOOD_PICKAXE("Wooden Pickaxe", "Steal a wooden pickaxe.", Material.WOOD_PICKAXE, 30),
    WOOD_AXE("Wooden Axe", "Steal a wooden axe.", Material.WOOD_AXE, 30),
    STONE_PICKAXE("Stone Pickaxe", "Steal a stone pickaxe.", Material.STONE_PICKAXE, 30),
    STONE_AXE("Stone Axe", "Steal a stone axe.", Material.STONE_AXE, 30),
    EYE_OF_ENDER("Eye of Ender", "Steal an eye of ender.", Material.EYE_OF_ENDER, 35),
    REDSTONE_BLOCK("Redstone Block", "Steal a redstone block.", Material.REDSTONE_BLOCK, 36),
    GOLD_INGOT("Gold Ingot", "Steal a gold ingot.", Material.GOLD_INGOT, 40),
    QUARTZ_ORE("Nether Quartz Ore", "Steal nether quartz ore.", Material.QUARTZ_ORE, 40),
    QUARTZ_BLOCK("Nether Quartz Block", "Steal a nether quartz block.", Material.QUARTZ_BLOCK, 40),
    BUCKET("Bucket", "Steal a bucket.", Material.BUCKET, 40),
    SPECKLED_MELON("Glistering Melon", "Steal glistering melon.", Material.SPECKLED_MELON, 40),
    IRON_SWORD("Iron Sword", "Steal an iron sword.", Material.IRON_SWORD, 40),
    MILK("Milk", "Steal a milk bucket.", Material.MILK_BUCKET, 45),
    WATER_BUCKET("Water Bucket", "Steal a water bucket.", Material.WATER_BUCKET, 45),
    LAVA_BUCKET("Lava Bucket", "Steal a lava bucket.", Material.LAVA_BUCKET, 45),
    BLAZE_ROD("Blaze Rod", "Steal a blaze rod.", Material.BLAZE_ROD, 50),
    MAGMA_CREAM("Magma Cream", "Steal magma cream.", Material.MAGMA_CREAM, 50),
    IRON_HOE("Iron Hoe", "Steal an iron hoe.", Material.IRON_HOE, 50),
    BREWING_STAND_ITEM("Brewing Stand", "Steal a brewing stand.", Material.BREWING_STAND_ITEM, 55),
    REDSTONE_ORE("Redstone Ore", "Steal redstone ore.", Material.REDSTONE_ORE, 60),
    APPLE("Apple", "Steal an apple.", Material.APPLE, 60),
    GOLD_SPADE("Golden Shovel", "Steal a golden shovel.", Material.GOLD_SPADE, 60),
    COMPASS("Compass", "Steal a compass.", Material.COMPASS, 65),
    IRON_PICKAXE("Iron Pickaxe", "Steal an iron pickaxe.", Material.IRON_PICKAXE, 65),
    IRON_AXE("Iron Axe", "Steal an iron axe.", Material.IRON_AXE, 65),
    LAPIS_BLOCK("Lapis Lazuli Block", "Steal a lapis lazuli block.", Material.LAPIS_BLOCK, 70),
    COAL_BLOCK("Coal Block", "Steal a coal block.", Material.COAL_BLOCK, 72),
    GOLD_SWORD("Gold Sword", "Steal a gold sword.", Material.GOLD_SWORD, 90),
    EMERALD("Emerald", "Steal an emerald.", Material.EMERALD, 100),
    GOLD_HOE("Golden Hoe", "Steal a golden hoe.", Material.GOLD_HOE, 100),
    LAPIS_ORE("Lapis Lazuli Ore", "Steal lapis lazuli ore.", Material.LAPIS_ORE, 120),
    IRON_BLOCK("Iron Block", "Steal an iron block.", Material.IRON_BLOCK, 135),
    DIAMOND("Diamond", "Steal a diamond.", Material.DIAMOND, 160),
    CLOCK("Clock", "Steal a clock.", Material.WATCH, 165),
    DIAMOND_SPADE("Diamond Shovel", "Steal a diamond shovel.", Material.DIAMOND_SPADE, 180),
    GHAST_TEAR("Ghast Tear", "Steal a ghast tear.", Material.GHAST_TEAR, 200),
    SEA_LANTER("Sea Lantern", "Steal a sea lantern.", Material.SEA_LANTERN, 200),
    DIAMOND_SWORD("Diamond Sword", "Steal a diamond sword.", Material.DIAMOND_SWORD, 330),
    DIAMOND_HOE("Diamond Hoe", "Steal a diamond hoe.", Material.DIAMOND_HOE, 340),
    GOLD_BLOCK("Gold Block", "Steal a gold block.", Material.GOLD_BLOCK, 360),
    GOLDEN_APPLE("Golden Apple", "Steal a golden apple.", Material.GOLDEN_APPLE, 400),
    DIAMOND_PICKAXE("Diamond Pickaxe", "Steal a diamond pickaxe.", Material.DIAMOND_PICKAXE, 500),
    DIAMOND_AXE("Diamond Axe", "Steal a diamond axe.", Material.DIAMOND_AXE, 500),
    DIAMOND_ORE("Diamond Ore", "Steal diamond ore.", Material.DIAMOND_ORE, 640),
    EMERALD_ORE("Emerald Ore", "Steal an emerald ore.", Material.EMERALD_ORE, 800),
    EMERALD_BLOCK("Emerald Block", "Steal an emerald block.", Material.EMERALD_BLOCK, 900),
    DIAMOND_BLOCK("Diamond Block", "Steal a diamond block.", Material.DIAMOND_BLOCK, 1450);

    private String name;
    private String rawName;
    private String description;
    private Material material;
    private ItemStack lockedItemStack;
    private ItemStack itemStack;
    private double chance;
    private int value;
    private final int maxValue = 2_000;
    private DecimalFormat decimalFormat = new DecimalFormat("#.###%");

    PickpocketItem(String name, String description, Material material, int value) {
        this.description = description;
        this.material = material;
        this.value = value;
        this.rawName = name;

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

    public String getDescription() {
        return description;
    }

    public ItemStack getLockedItemStack(Profile profile) {
        lockedItemStack = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta lockedItemStackItemMeta = lockedItemStack.getItemMeta();
        lockedItemStackItemMeta.setDisplayName(name);
        ArrayList lockedItemStackLoreList = Lists.<String>newArrayList();
        lockedItemStackLoreList.add(ChatColor.DARK_GRAY + description);
        lockedItemStackLoreList.add(ChatColor.GRAY + "Chance: " + ChatColor.WHITE + decimalFormat.format(chance));
        lockedItemStackLoreList.add(ChatColor.GRAY + "Your chance: " + ChatColor.WHITE + decimalFormat.format(calculateStolenBasedChance(profile.getTimesStolenOf(this))));
        lockedItemStackLoreList.add(ChatColor.GRAY + "Times Stolen: " + ChatColor.WHITE + NumberFormat.getInstance().format(profile.getTimesStolenOf(this)));
        lockedItemStackItemMeta.setLore(lockedItemStackLoreList);
        lockedItemStack.setItemMeta(lockedItemStackItemMeta);
        return lockedItemStack;
    }

    public ItemStack getItemStack(Profile profile) {
        itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(getLockedItemStack(profile).getItemMeta().getLore());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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
