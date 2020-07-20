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

    POISONOUS_POTATO("Poisonous Potato", new ItemStack(Material.POISONOUS_POTATO), 1),
    COOKIE("Cookie", new ItemStack(Material.COOKIE), 1),
    NETHERRACK("Netherrack", "Steal netherrack.", Material.NETHERRACK, 1),
    ROTTEN_FLESH("Rotten Flesh", "Steal rotten flesh.", Material.ROTTEN_FLESH, 1),
    SNOW_BALL("Snowball", "Steal a snowball.", Material.SNOWBALL, 2),
    MELON("Melon Slice", "Steal a melon slice.", Material.MELON_SLICE, 2),
    SEEDS("Seeds", "Steal seeds.", Material.WHEAT_SEEDS, 2),
    MELON_SEEDS("Melon Seeds", "Steal melon seeds.", Material.MELON_SEEDS, 2),
    COCOA("Cocoa Beans", "Steal cocoa beans.", Material.COCOA, 2),
    WHEAT("Wheat", "Steal wheat.", Material.WHEAT, 2),
    SUGAR_CANE("Sugar Cane", "Steal sugar cane.", Material.SUGAR_CANE, 2),
    SUGAR("Sugar", "Steal sugar.", Material.SUGAR, 2),
    EGG("Egg", "Steal an egg.", Material.EGG, 2),
    CLAY_BALL("Clay", "Steal clay.", Material.CLAY_BALL, 3),
    PUMPKIN_SEEDS("Pumpkin Seeds", "Steal pumpkin seeds.", Material.PUMPKIN_SEEDS, 3),
    GLASS_BOTTLE("Glass Bottle", "Steal a glass bottle.", Material.GLASS_BOTTLE, 3),
    ARROW("Arrow", "Steal an arrow.", Material.ARROW, 3),
    REDSTONE("Redstone Dust", "Steal redstone dust.", Material.REDSTONE, 4),
    SNOW_BLOCK("Snow Block", "Steal a snow block.", Material.SNOW_BLOCK, 4),
    GLOWSTONE_DUST("Glowstone Dust", "Steal glowstone dust", Material.GLOWSTONE_DUST, 4),
    POTATO_ITEM("Raw Potato", "Steal a raw potato.", Material.POTATO, 4),
    SOUL_SAND("Sould sand", "Steal soul sand.", Material.SOUL_SAND, 4),
    CARROT_ITEM("Carrot", "Steal a carrot.", Material.CARROT, 4),
    FLINT("Flint", "Steal flint.", Material.FLINT, 4),
    FEATHER("Feather", "Steal a feather.", Material.FEATHER, 4),
    STRING("String", "Steal string.", Material.STRING, 4),
    NETHER_WARTS("Nether Wart", "Steal nether warts.", Material.NETHER_WART, 4),
    MOSSY_COBBLESTONE("Mossy Cobble", "Steal mossy cobblestone.", Material.MOSSY_COBBLESTONE, 5),
    BAKED_POTATO("Baked Potato", "Steal a baked potato.", Material.BAKED_POTATO, 5),
    BREAD("Bread", "Steal bread.", Material.BREAD, 5),
    GOLD_NUGGET("Gold Nugget", "Steal a gold nugget.", Material.GOLD_NUGGET, 5),
    ICE("Ice", "Steal ice.", Material.ICE, 6),
    BONE("Bone", "Steal a bone.", Material.BONE, 6),
    COAL("Coal", "Steal coal.", Material.COAL, 8),
    RAW_CHICKEN("Raw Chicken", "Steal chicken.", Material.CHICKEN, 8),
    SPIDER_EYE("Spider Eye", "Steal a spider eye.", Material.SPIDER_EYE, 8),
    COOKED_CHICKEN("Cooked Chicken", "Steal cooked chickcen.", Material.COOKED_CHICKEN, 9),
    QUARTZ("Nether Quartz", "Steal nether quartz.", Material.QUARTZ, 10),
    MYCEL("Mycelium", "Steal mycelium.", Material.MYCELIUM, 10),
    RAW_BEEF("Raw Beef", "Steal raw beef.", Material.BEEF, 10),
    PUMPKIN("Pumpkin", "Steal a pumpkin.", Material.PUMPKIN, 10),
    SULPHUR("Gunpowder", "Steal gunpowder.", Material.GUNPOWDER, 10),
    ENDER_PEARL("Ender Pearl", "Steal an ender pearl.", Material.ENDER_PEARL, 10),
    FISHING_ROD("Fishing Rod", "Steal a fishing rod.", Material.FISHING_ROD, 10),
    COOKED_BEEF("Cooked Beef", "Steal cooked beef.", Material.COOKED_BEEF, 11),
    CLAY("Clay Block", "Steal a clay block.", Material.CLAY, 12),
    OBSIDIAN("Obsidian", "Steal obsidian.", Material.OBSIDIAN, 12),
    PORK("Raw Porkchop", "Steal raw porkchop.", Material.PORKCHOP, 12),
    GRILLED_PORK("Grilled Porkchop", "Steal a grilled porkchop.", Material.COOKED_PORKCHOP, 13),
    GLOWSTONE("Glowstone Block", "Steal a glowstone block", Material.GLOWSTONE, 15),
    IRON_INGOT("Iron Ingot", "Steal an iron ingot.", Material.IRON_INGOT, 15),
    MUSHROOM_SOUP("Mushroom Stew", "Steal mushroom stew.", Material.MUSHROOM_STEW, 15),
    PUMPKIN_PIE("Pumpkin Pie", "Steal a pumpkin pie.", Material.PUMPKIN_PIE, 15),
    WOOD_SWORD("Wooden Sword", "Steal a wooden sword.", Material.WOODEN_SWORD, 15),
    WOOD_SPADE("Wooden Shovel", "Steal a wooden shovel.", Material.WOODEN_SHOVEL, 15),
    STONE_SWORD("Stone Sword", "Steal a stone sword.", Material.STONE_SWORD, 15),
    STONE_SPADE("Stone Shovel", "Steal a stone shovel.", Material.STONE_SHOVEL, 15),
    FERMENTED_SPIDER_EYE("Fermented Spider Eye", "Steal a fermented spider eye", Material.FERMENTED_SPIDER_EYE, 18),
    ENDSTONE("Endstone", "Steal endstone.", Material.END_STONE, 20),
    RAW_FISH("Raw Fish", "Steal raw fish.", Material.LEGACY_RAW_FISH, 20),
    HAY_BLOCK("Hay Bale", "Steal a hay bale.", Material.HAY_BLOCK, 20),
    LEATHER("Leather", "Steal leather.", Material.LEATHER, 20),
    BOW("Bow", "Steal a bow.", Material.BOW, 20),
    FLINT_AND_STEEL("Flint and Steel", "Steal flint and steel.", Material.FLINT_AND_STEEL, 20),
    COOKED_FISH("Cooked Fish", "Steal cooked fish.", Material.LEGACY_COOKED_FISH, 21),
    MELON_BLOCK("Melon Block", "Steal a melon block.", Material.MELON, 25),
    SLIME_BALL("Slimeball", "Steal a slimeball.", Material.SLIME_BALL, 25),
    BLAZE_POWDER("Blaze Poweder", "Steal blaze powder.", Material.BLAZE_POWDER, 25),
    IRON_SPADE("Iron Shovel", "Steal an iron shovel.", Material.IRON_SHOVEL, 25),
    WOOD_HOE("Wood Hoe", "Steal a wooden hoe.", Material.WOODEN_HOE, 25),
    STONE_HOE("Stone Hoe", "Steal a stone hoe.", Material.STONE_HOE, 25),
    CAKE("Cake", "Steal a cake.", Material.CAKE, 30),
    SHEARS("Shears", "Steal a pair of shears.", Material.SHEARS, 30),
    WOOD_PICKAXE("Wooden Pickaxe", "Steal a wooden pickaxe.", Material.WOODEN_PICKAXE, 30),
    WOOD_AXE("Wooden Axe", "Steal a wooden axe.", Material.WOODEN_AXE, 30),
    STONE_PICKAXE("Stone Pickaxe", "Steal a stone pickaxe.", Material.STONE_PICKAXE, 30),
    STONE_AXE("Stone Axe", "Steal a stone axe.", Material.STONE_AXE, 30),
    EYE_OF_ENDER("Eye of Ender", "Steal an eye of ender.", Material.ENDER_EYE, 35),
    REDSTONE_BLOCK("Redstone Block", "Steal a redstone block.", Material.REDSTONE_BLOCK, 36),
    GOLD_INGOT("Gold Ingot", "Steal a gold ingot.", Material.GOLD_INGOT, 40),
    QUARTZ_ORE("Nether Quartz Ore", "Steal nether quartz ore.", Material.NETHER_QUARTZ_ORE, 40),
    QUARTZ_BLOCK("Nether Quartz Block", "Steal a nether quartz block.", Material.QUARTZ_BLOCK, 40),
    BUCKET("Bucket", "Steal a bucket.", Material.BUCKET, 40),
    SPECKLED_MELON("Glistering Melon", "Steal glistering melon.", Material.GLISTERING_MELON_SLICE, 40),
    IRON_SWORD("Iron Sword", "Steal an iron sword.", Material.IRON_SWORD, 40),
    MILK("Milk", "Steal a milk bucket.", Material.MILK_BUCKET, 45),
    WATER_BUCKET("Water Bucket", "Steal a water bucket.", Material.WATER_BUCKET, 45),
    LAVA_BUCKET("Lava Bucket", "Steal a lava bucket.", Material.LAVA_BUCKET, 45),
    BLAZE_ROD("Blaze Rod", "Steal a blaze rod.", Material.BLAZE_ROD, 50),
    MAGMA_CREAM("Magma Cream", "Steal magma cream.", Material.MAGMA_CREAM, 50),
    IRON_HOE("Iron Hoe", "Steal an iron hoe.", Material.IRON_HOE, 50),
    BREWING_STAND_ITEM("Brewing Stand", "Steal a brewing stand.", Material.LEGACY_BREWING_STAND_ITEM, 55),
    REDSTONE_ORE("Redstone Ore", "Steal redstone ore.", Material.REDSTONE_ORE, 60),
    APPLE("Apple", "Steal an apple.", Material.APPLE, 60),
    GOLD_SPADE("Golden Shovel", "Steal a golden shovel.", Material.GOLDEN_SHOVEL, 60),
    COMPASS("Compass", "Steal a compass.", Material.COMPASS, 65),
    IRON_PICKAXE("Iron Pickaxe", "Steal an iron pickaxe.", Material.IRON_PICKAXE, 65),
    IRON_AXE("Iron Axe", "Steal an iron axe.", Material.IRON_AXE, 65),
    LAPIS_BLOCK("Lapis Lazuli Block", "Steal a lapis lazuli block.", Material.LAPIS_BLOCK, 70),
    COAL_BLOCK("Coal Block", "Steal a coal block.", Material.COAL_BLOCK, 72),
    GOLD_SWORD("Gold Sword", "Steal a gold sword.", Material.GOLDEN_SWORD, 90),
    EMERALD("Emerald", "Steal an emerald.", Material.EMERALD, 100),
    GOLD_HOE("Golden Hoe", "Steal a golden hoe.", Material.GOLDEN_HOE, 100),
    LAPIS_ORE("Lapis Lazuli Ore", "Steal lapis lazuli ore.", Material.LAPIS_ORE, 120),
    DIAMOND("Diamond", "Steal a diamond.", Material.DIAMOND, 160),
    CLOCK("Clock", "Steal a clock.", Material.CLOCK, 165),
    DIAMOND_SPADE("Diamond Shovel", "Steal a diamond shovel.", Material.DIAMOND_SHOVEL, 180),
    GHAST_TEAR("Ghast Tear", "Steal a ghast tear.", Material.GHAST_TEAR, 200),
    SEA_LANTER("Sea Lantern", "Steal a sea lantern.", Material.SEA_LANTERN, 200),
    DIAMOND_SWORD("Diamond Sword", "Steal a diamond sword.", Material.DIAMOND_SWORD, 330),
    DIAMOND_HOE("Diamond Hoe", "Steal a diamond hoe.", Material.DIAMOND_HOE, 340),
    GOLDEN_APPLE("Golden Apple", "Steal a golden apple.", Material.GOLDEN_APPLE, 400),
    DIAMOND_PICKAXE("Diamond Pickaxe", "Steal a diamond pickaxe.", Material.DIAMOND_PICKAXE, 500),
    DIAMOND_AXE("Diamond Axe", "Steal a diamond axe.", Material.DIAMOND_AXE, 500),
    DIAMOND_ORE("Diamond Ore", "Steal diamond ore.", Material.DIAMOND_ORE, 640),
    EMERALD_ORE("Emerald Ore", "Steal an emerald ore.", Material.EMERALD_ORE, 800),
    EMERALD_BLOCK("Emerald Block", "Steal an emerald block.", Material.EMERALD_BLOCK, 900),
    DIAMOND_BLOCK("Diamond Block", "Steal a diamond block.", Material.DIAMOND_BLOCK, 1450),

    STONE("Stone", new ItemStack(Material.STONE), 2),
    GRASS("Grass", new ItemStack(Material.GRASS_BLOCK), 2),
    DIRT("Dirt", new ItemStack(Material.DIRT), 1),
    COBBLESTONE("Cobblestone", new ItemStack(Material.COBBLESTONE), 1),
    OAK_PLANK("Oak Plank", new ItemStack(Material.OAK_PLANKS), 2),
    BIRCH_PLANK("Birch Plank", new ItemStack(Material.BIRCH_PLANKS), 2),
    JUNGLE_PLANK("Jungle Plank", new ItemStack(Material.JUNGLE_PLANKS), 2),
    OAK_SAPLING("Oak Sapling", new ItemStack(Material.OAK_SAPLING), 1),
    SPRUCE_SAPLING("Spruce Sapling", new ItemStack(Material.SPRUCE_SAPLING), 1),
    BIRCH_SAPLING("Birch Sapling", new ItemStack(Material.BIRCH_SAPLING), 1),
    JUNGLE_SAPLING("Jungle Sapling", new ItemStack(Material.JUNGLE_SAPLING), 1),
    BEDROCK("Bedrock", new ItemStack(Material.BEDROCK), 0),
    SAND("Sand", new ItemStack(Material.SAND), 1),
    GRAVEL("Gravel", new ItemStack(Material.GRAVEL), 1),
    GOLD_ORE("Gold Ore", new ItemStack(Material.GOLD_ORE), 1000),
    IRON_ORE("Iron Ore", new ItemStack(Material.IRON_ORE), 300),
    COAL_ORE("Coal Ore", new ItemStack(Material.COAL_ORE), 140),
    OAK_WOOD("Oak Wood", new ItemStack(Material.OAK_WOOD), 8),
    SPRUCE_WOOD("Spruce Wood", new ItemStack(Material.SPRUCE_WOOD, 1, (short) 1), 8),
    BIRCH_WOOD("Birch Wood", new ItemStack(Material.BIRCH_WOOD, 1, (short) 2), 8),
    JUNGLE_WOOD("Jungle Wood", new ItemStack(Material.JUNGLE_WOOD, 1, (short) 3), 8),
    OAK_LEAVES("Oak Leaves", new ItemStack(Material.OAK_LEAVES), 0.4),
    SPRUCE_LEAVES("Spruce Leaves", new ItemStack(Material.SPRUCE_LEAVES), 0.40),
    BIRCH_LEAVES("Birch Leaves", new ItemStack(Material.BIRCH_LEAVES), 0.40),
    JUNGLE_LEAVES("Jungle Leaves", new ItemStack(Material.JUNGLE_LEAVES), 0.40),
    SPONGE("Sponge", new ItemStack(Material.SPONGE), 100_000),
    GLASS("Glass", new ItemStack(Material.GLASS), 17),
    LAPIS_LAZULI_ORE("Lapis Lazuli Ore", new ItemStack(Material.LAPIS_ORE), 300),
    LAPIS_LAZULI_BLOCK("Lapis Lazuli Block", new ItemStack(Material.LAPIS_BLOCK), 385.71),
    DISPENSER("Dispenser", new ItemStack(Material.DISPENSER), 70),
    SANDSTONE("Sandstone", new ItemStack(Material.SANDSTONE), 4),
    CHISELED_SANDSTONE("Chiseled Sandstone", new ItemStack(Material.CHISELED_SANDSTONE, 1, (short) 1), 4),
    SMOOTH_SANDSTONE("Smooth Sandstone", new ItemStack(Material.SMOOTH_SANDSTONE, 1, (short) 2), 4),
    NOTE_BLOCK("Note Block", new ItemStack(Material.NOTE_BLOCK), 46),
    POWERED_RAIL("Powered Rail", new ItemStack(Material.POWERED_RAIL), 905.17),
    DEETECTOR_RAIL("Detector Rail", new ItemStack(Material.DETECTOR_RAIL), 275.67),
    STICKY_PISTON("Sticky Piston", new ItemStack(Material.STICKY_PISTON), 314),
    WEB("Web", new ItemStack(Material.COBWEB), 100),
    DEAD_SHRUB("Dead Shrub", new ItemStack(Material.DEAD_BUSH), 4),
    TALL_GRASS("Tall Grass", new ItemStack(Material.TALL_GRASS), 4),
    FERN("Fern", new ItemStack(Material.FERN), 4),
    //Another dead shrub?
    PISTON("Piston", new ItemStack(Material.PISTON), 310),
    WOOL("Wool", new ItemStack(Material.WHITE_WOOL), 40),
    ORANGE_WOOL("Orange Wool", new ItemStack(Material.ORANGE_WOOL), 49),
    MAGENTA_WOOL("Magenta Wool", new ItemStack(Material.MAGENTA_WOOL), 56.38),
    LIGHT_BLUE_WOOL("Light Blue Wool", new ItemStack(Material.LIGHT_BLUE_WOOL), 62.76),
    YELLOW_WOOL("Yellow Wool", new ItemStack(Material.YELLOW_WOOL), 48),
    LIME_WOOL("Lime Wool", new ItemStack(Material.LIME_WOOL), 59.33),
    PINK_WOOL("Pink Wool", new ItemStack(Material.PINK_WOOL), 46.33),
    GRAY_WOOL("Gray Wool", new ItemStack(Material.GRAY_WOOL), 51.33),
    LIGHT_GRAY_WOOL("Light Gray Wool", new ItemStack(Material.LIGHT_GRAY_WOOL), 48.44),
    CYAN_WOOL("Cyan Wool", new ItemStack(Material.CYAN_WOOL), 79.43),
    PURPLE_WOOL("Purple Wool", new ItemStack(Material.PURPLE_WOOL), 66.43),
    BLUE_WOOL("Blue Wool", new ItemStack(Material.BLUE_WOOL), 82.86),
    BROWN_WOOL("Brown Wool", new ItemStack(Material.BROWN_WOOL), 44),
    GREEN_WOOL("Green Wool", new ItemStack(Material.GREEN_WOOL), 76),
    RED_WOOL("Red Wool", new ItemStack(Material.RED_WOOL), 50),
    BLACK_WOOL("Black Wool", new ItemStack(Material.BLACK_WOOL), 60),
    DANDELION("Dandelion", new ItemStack(Material.DANDELION), 16),
    ROSE("Rose", new ItemStack(Material.POPPY), 20),
    BROWN_MUSHROOM("Brown Mushroom", new ItemStack(Material.BROWN_MUSHROOM), 16),
    RED_MUSHROOM("Red Mushroom", new ItemStack(Material.RED_MUSHROOM), 20),
    GOLD_BLOCK("Gold Block", new ItemStack(Material.GOLD_BLOCK), 8100),
    IRON_BLOCK("Iron Block", new ItemStack(Material.IRON_BLOCK), 2430),
    STONE_SLAB("Stone Slab", new ItemStack(Material.STONE_SLAB), 1),
    SANDSTONE_SLAB("Sandstone Slab", new ItemStack(Material.SANDSTONE_SLAB), 2),
    WOODEN_SLAB("Wooden Slab", new ItemStack(Material.OAK_SLAB), 1),
    COBBLESTONE_SLAB("Cobblestone Slab", new ItemStack(Material.COBBLESTONE_SLAB), 0.50),
    BRICK_SLAB("Brick Slab", new ItemStack(Material.BRICK_SLAB), 52),
    STONE_BRICK_SLAB("Stone Brick Slab", new ItemStack(Material.STONE_BRICK_SLAB), 1),
    NETHER_BRICK_SLAB("Nether Brick Slab", new ItemStack(Material.NETHER_BRICK_SLAB), 32.80),
    QUARTZ_SLAB("Quartz Slab", new ItemStack(Material.QUARTZ_SLAB), 140),
    BRICK("Brick", new ItemStack(Material.BRICK), 104);

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

    PickpocketItem(String name, String description, Material material, double value){
        this.itemStack = new ItemStack(material);
        this.material = material;
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
        lockedItemStack = new ItemStack(Material.BARRIER);
        ItemMeta lockedItemStackItemMeta = lockedItemStack.getItemMeta();
        lockedItemStackItemMeta.setDisplayName(name);
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
        return (((maxValue - value) * 10) / maxValue) / 100;
    }
}
