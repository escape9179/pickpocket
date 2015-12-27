package logan.pickpocket;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
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

    COBBLESTONE("Cobblestone", "Steal cobblestone.", Material.COBBLESTONE, 0.15D),
    DIRT("Dirt", "Steal dirt.", Material.DIRT, 0.13D),
    GRAVEL("Gravel", "Steal gravel.", Material.GRAVEL, 0.13D),
    STONE("Stone", "Steal stone.", Material.STONE, 0.12D),
    GRASS("Grass", "Steal grass.", Material.GRASS, 0.12D),
    LEAVES("Leaves", "Steal leaves.", Material.LEAVES, 0.12D),
    SAND("Sand", "Steal sand.", Material.SAND, 0.12D),
    GLASS("Glass","Steal glass.",Material.GLASS,0.11D),
    WOOD("Planks", "Steal wood planks.", Material.WOOD, 0.11D),
    SAPLING("Sapling", "Steal a sapling.", Material.SAPLING, 0.10D),
    WOOD_SWORD("Wooden Sword", "Steal a wooden sword.", Material.WOOD_SWORD, 0.08D),
    LOG("Log", "Steal a log.", Material.LOG, 0.075D),
    COAL_ORE("Coal Ore", "Steal coal ore.", Material.COAL_ORE, 0.07D),
    COAL("Coal", "Steal coal.", Material.COAL, 0.07D),
    IRON_ORE("Iron Ore", "Steal iron ore.", Material.IRON_ORE, 0.05D),
    IRON_INGOT("Iron Ingot", "Steal an iron ingot.", Material.IRON_INGOT, 0.05D),
    STONE_SWORD("Stone Sword", "Steal a stone sword.", Material.STONE_SWORD, 0.05D),
    LEATHER_HELMET("Leather Helmet", "Steal a leather helmet", Material.LEATHER_HELMET, 0.05D),
    LEATHER_CHESTPLATE("Leather Chestplate", "Steal a leather chestplate", Material.LEATHER_CHESTPLATE, 0.05D),
    LEATHER_LEGGINGS("Leather Leggings", "Steal a pair of leather leggings.", Material.LEATHER_LEGGINGS, 0.05D),
    LEATHER_BOOTS("Leather Boots", "Steal a pair of leather boots.", Material.LEATHER_BOOTS, 0.05D),
    BOW("Bow", "Steal a bow.", Material.BOW, 0.04D),
    SPONGE("Sponge", "Steal a sponge.", Material.SPONGE, 0.04D),
    IRON_SWORD("Iron Sword", "Steal an iron sword.", Material.IRON_SWORD, 0.03D),
    GOLD_ORE("Gold Ore", "Steal gold ore.", Material.GOLD_ORE, 0.03D),
    GOLD_INGOT("Gold Ingot", "Steal a gold ingot.", Material.GOLD_INGOT, 0.03D),
    GOLD_SWORD("Gold Sword", "Steal a gold sword.", Material.GOLD_SWORD, 0.03D),
    IRON_HELMET("Iron Helmet", "Steal an iron helmet.", Material.IRON_HELMET, 0.025D),
    IRON_CHESTPLATE("Iron Chestplate", "Steal an iron chestplate.", Material.IRON_CHESTPLATE, 0.025D),
    IRON_LEGGINGS("Iron Leggings", "Steal a pair of iron leggings.", Material.IRON_LEGGINGS, 0.025D),
    IRON_BOOTS("Iron Boots", "Steal a pair of iron boots.", Material.IRON_BOOTS, 0.025D),
    OBSIDIAN("Obsidian", "Steal obsidian.", Material.OBSIDIAN, 0.02D),
    ENDER_PEARL("Ender Pearl", "Steal an ender peal.", Material.ENDER_PEARL, 0.02D),
    LAPIS_ORE("Lapis Lazuli", "Steal lapiz lazuli.", Material.LAPIS_ORE, 0.02D),
    DIAMOND("Diamond", "Steal a diamond.", Material.DIAMOND, 0.019D),
    LAPIS_BLOCK("Lapis Block","Steal a lapis block.",Material.LAPIS_BLOCK,0.018D),
    DIAMOND_SWORD("Diamond Sword", "Steal a diamond sword.", Material.DIAMOND_SWORD, 0.018D),
    DIAMOND_HELMET("Diamond Helmet", "Steal a diamond helmet.", Material.DIAMOND_HELMET, 0.017D),
    DIAMOND_CHESTPLATE("Diamond Chestplate", "Steal a diamond chestplate", Material.DIAMOND_CHESTPLATE, 0.017D),
    DIAMOND_LEGGINGS("Diamond Leggings", "Steal a pair of diamond leggings.", Material.DIAMOND_LEGGINGS, 0.017D),
    DIAMOND_BOOTS("Diamond Boots", "Steal a pair of diamonds boots.", Material.DIAMOND_BOOTS, 0.017D),
    BEDROCK("Bedrock", "Steal bedrock.", Material.BEDROCK, 0.01D),
    DRAGON_EGG("Dragon Egg", "Steal a dragon egg.", Material.DRAGON_EGG, 0.002D);



    private String name;
    private String description;
    private Material material;
    private ItemStack lockedItemStack;
    private ItemStack itemStack;
    private double chance;
    private int experienceValue;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##%");

    PickpocketItem(String name, String description, Material material, double chance) {
        this.description = description;
        this.material = material;
        this.chance = chance;
        this.name = name;

        if (chance <= 0.005D) this.name = ChatColor.LIGHT_PURPLE + name;
        else if (chance <= 0.01D) this.name = ChatColor.DARK_AQUA + name;
        else if (chance <= 0.025D) this.name = ChatColor.AQUA + name;
        else if (chance <= 0.05D) this.name = ChatColor.BLUE + name;
        else if (chance <= 0.1D) this.name = ChatColor.GOLD + name;
        else this.name = ChatColor.GRAY + name;

        this.experienceValue = (int) (((1 - chance) * 100) / (chance * 2));
    }

    public String getName() {
        return name;
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
        lockedItemStackLoreList.add(ChatColor.GRAY + "Your chance: " + ChatColor.WHITE + decimalFormat.format(calculateExperienceBasedChance(profile.getExperience())));
        lockedItemStackLoreList.add(ChatColor.GRAY + "Experience: " + ChatColor.WHITE + Integer.toString(experienceValue));
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

    public double getChance() {
        return chance;
    }

    public double calculateExperienceBasedChance(int playerExperience) {
        return ((playerExperience / (experienceValue / chance)) / 100) + chance;
    }

    public int getExperienceValue() {
        return experienceValue;
    }
}
