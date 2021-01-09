package logan.api.gui;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tre Logan
 */
public final class MenuItem implements MenuItemClickListener {

    private MenuItemClickListener listener;
    private ItemStack             itemStack;

    public MenuItem(String name, ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        setName(name);
    }

    public MenuItem(String name) {
        this(name, new ItemStack(Material.DIRT));
    }

    public MenuItem(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public final MenuItem setMaterial(Material material) {
        itemStack.setType(material);
        return this;
    }

    public final MenuItem setDurability(int durability) {
        setDurability((short) durability);
        return this;
    }

    public final MenuItem setDurability(short durability) {
        itemStack.setDurability(durability);
        return this;
    }

    public final MenuItem setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public final MenuItem clearName() {
        setName("");
        return this;
    }

    public final MenuItem setName(String name) {
        setMetaProperty(m -> {
            m.setDisplayName(ChatColor.WHITE + name);
            return m;
        });
        return this;
    }

    public final MenuItem addItemFlags(Set<ItemFlag> flags) {
        ItemFlag[] flagArray = new ItemFlag[flags.size()];
        addItemFlags(flags.toArray(flagArray));
        return this;
    }

    public final MenuItem addItemFlags(ItemFlag... flags) {
        setMetaProperty(m -> {
            m.addItemFlags(flags);
            return m;
        });
        return this;
    }

    public final MenuItem setMagic(boolean magic) {
        addEnchantment(Enchantment.ARROW_DAMAGE, 0, false);
        addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public final MenuItem addEnchantment(Enchantment enchantment, int level, boolean bypass) {
        setMetaProperty(m -> {
            m.addEnchant(enchantment, level, bypass);
            return m;
        });
        return this;
    }

    public final MenuItem addEnchantments(Map<Enchantment, Integer> enchantments) {
        setMetaProperty(m -> {
            enchantments.forEach((e, l) -> m.addEnchant(e, l, true));
            return m;
        });
        return this;
    }

    public final MenuItem setLore(ChatColor color, String... lore) {
        setLore(color, Arrays.asList(lore));
        return this;
    }

    public final MenuItem setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public final MenuItem setLore(ChatColor color, List<String> lore) {
        setMetaProperty(m -> {
            m.setLore(lore.stream()
                    .map(l -> color + l)
                    .collect(Collectors.toList()));
            return m;
        });
        return this;
    }

    public final MenuItem setLore(List<String> lore) {
        setLore(ChatColor.WHITE, lore);
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDisplayName() {
        return getMetaProperty(ItemMeta::getDisplayName);
    }

    public Material getMaterial() {
        return itemStack.getType();
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public short getDurability() {
        return itemStack.getDurability();
    }

    public List<String> getLore() {
        return getMetaProperty(m -> (m.hasLore()) ? m.getLore() : Lists.newArrayList());
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return getMetaProperty(ItemMeta::getEnchants);
    }

    public Set<ItemFlag> getItemFlags() {
        return getMetaProperty(ItemMeta::getItemFlags);
    }

    private void setMetaProperty(Function<ItemMeta, ItemMeta> function) {
        itemStack.setItemMeta(function.apply(itemStack.getItemMeta()));
    }

    private <T> T getMetaProperty(Function<ItemMeta, T> function) {
        return function.apply(itemStack.getItemMeta());
    }

    public MenuItem addListener(MenuItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    public MenuItemClickListener getListener() {
        return listener;
    }

    @Override
    public void onClick(MenuItemClickEvent event) {
        if (listener != null) {
            listener.onClick(event);
        }
    }

}
