package me.scifi.hcf.abilities;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Item {

    SNOWPORT("SNOWPORT", HCF.getPlugin().getItemsYML().getString("snowport.name"), HCF.getPlugin().getItemsYML().getStringList("snowport.lore"), new ItemStack(Material.SNOW_BALL,1), HCF.getPlugin().getItemsYML().getLong("snowport.cooldown"), HCF.getPlugin().getItemsYML().getBoolean("snowport.enabled")),
    BELCHBOMB("BELCHBOMB", HCF.getPlugin().getItemsYML().getString("belchbomb.name"), HCF.getPlugin().getItemsYML().getStringList("belchbomb.lore"), new ItemStack(Material.SLIME_BALL,1), HCF.getPlugin().getItemsYML().getLong("belchbomb.cooldown"), HCF.getPlugin().getItemsYML().getBoolean("belchbomb.enabled")),
    GRAPPLINGHOOK("GRAPPLINGHOOK", HCF.getPlugin().getItemsYML().getString("grapplinghook.name"), HCF.getPlugin().getItemsYML().getStringList("grapplinghook.lore"), new ItemStack(Material.FISHING_ROD), HCF.getPlugin().getItemsYML().getLong("grapplinghook.cooldown"), HCF.getPlugin().getItemsYML().getBoolean("grapplinghook.enabled"));
    private String name, display;

    private List<String> lore;

    private boolean enabled;

    private long cooldown;

    private ItemStack stack;

    Item(String name, String display, List<String> lore, ItemStack stack, long cooldown, boolean enabled) {
        this.name = name;
        this.display = display;
        this.lore = lore;
        this.stack = stack;
        this.cooldown = cooldown;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return Utils.chat(display);
    }

    public static List<String> getItemNames() {
        List<String> names = new ArrayList<>();
        Arrays.asList(values()).forEach(item -> names.add(item.getName()));
        return names;
    }

    public static Item getItem(String name){
        return Arrays.stream(values()).filter(item -> item.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<String> getLore() {
        return Utils.list(lore);
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSimilar(ItemStack toCompare) {
         return toCompare != null &&
                toCompare.getType().equals(stack.getType()) &&
                toCompare.hasItemMeta() &&
                toCompare.getItemMeta().hasDisplayName() &&
                toCompare.getItemMeta().getDisplayName().equals(Utils.chat(getDisplay())) &&
                toCompare.getItemMeta().hasLore() &&
                toCompare.getItemMeta().getLore().equals(Utils.list(getLore()));
    }

    public ItemStack getStack() {
        return stack;
    }
}
