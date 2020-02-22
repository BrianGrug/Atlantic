package me.scifi.hcf.signs;

import me.scifi.hcf.HCF;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kits {

    public static void giveDiamondKit(Player p){
        PlayerInventory inv = p.getInventory();
        p.getActivePotionEffects().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(20);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setGameMode(GameMode.SURVIVAL);
        ItemStack healing = new ItemStack(Material.POTION,1,(short)16421);
        ItemStack speed = new ItemStack(Material.POTION,1, (byte)8226);
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, HCF.getPlugin().getConfig().getInt("enchantment-limits.PROTECTION_ENVIRONMENTAL"));
        helmet.addEnchantment(Enchantment.DURABILITY, HCF.getPlugin().getConfig().getInt("enchantment-limits.DURABILITY"));
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, HCF.getPlugin().getConfig().getInt("enchantment-limits.PROTECTION_ENVIRONMENTAL"));
        chestplate.addEnchantment(Enchantment.DURABILITY, HCF.getPlugin().getConfig().getInt("enchantment-limits.DURABILITY"));
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, HCF.getPlugin().getConfig().getInt("enchantment-limits.PROTECTION_ENVIRONMENTAL"));
        leggings.addEnchantment(Enchantment.DURABILITY, HCF.getPlugin().getConfig().getInt("enchantment-limits.DURABILITY"));
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,HCF.getPlugin().getConfig().getInt("enchantment-limits.PROTECTION_ENVIRONMENTAL"));
        boots.addEnchantment(Enchantment.DURABILITY, HCF.getPlugin().getConfig().getInt("enchantment-limits.DURABILITY"));
        boots.addEnchantment(Enchantment.PROTECTION_FALL,4);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL,HCF.getPlugin().getConfig().getInt("enchantment-limits.DAMAGE_ALL"));
        sword.addEnchantment(Enchantment.DURABILITY,HCF.getPlugin().getConfig().getInt("enchantment-limits.DURABILITY"));
        ItemStack steak = new ItemStack(Material.COOKED_BEEF,64);
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL, 16);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(2, pearl);
        inv.setItem(8, speed);
        inv.setItem(9, speed);
        inv.setItem(18, speed);
        inv.setItem(27, speed);
        inv.setItem(28, speed);
        inv.setItem(1, steak);
        for(int i = 0; i < 34; i++){
            inv.addItem(healing);
        }
        p.updateInventory();
    }

    public static void giveBardKit(Player p){
        PlayerInventory inv = p.getInventory();
        p.getActivePotionEffects().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(20);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setGameMode(GameMode.SURVIVAL);
        ItemStack healing = new ItemStack(Material.POTION,1,(short)16421);
        ItemStack blaze = new ItemStack(Material.BLAZE_POWDER,32);
        ItemStack iron = new ItemStack(Material.IRON_INGOT,32);
        ItemStack sugar = new ItemStack(Material.SUGAR,32);
        ItemStack tear = new ItemStack(Material.GHAST_TEAR,16);
        ItemStack cream = new ItemStack(Material.MAGMA_CREAM,32);
        ItemStack feather = new ItemStack(Material.FEATHER, 32);
        ItemStack helmet = new ItemStack(Material.GOLD_HELMET,1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack chestplate = new ItemStack(Material.GOLD_CHESTPLATE,1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chestplate.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack leggings = new ItemStack(Material.GOLD_LEGGINGS,1);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL,4);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD,1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL,1);
        sword.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF,64);
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL, 16);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0,sword);
        inv.setItem(1, pearl);
        inv.setItem(2, sugar);
        inv.setItem(3, iron);
        inv.setItem(8, steak);
        inv.setItem(9, cream);
        inv.setItem(10, tear);
        inv.setItem(18, blaze);
        inv.setItem(27, feather);
        for(int i = 0; i < 34; i++){
            inv.addItem(healing);
        }
        p.updateInventory();
    }

    public static void giveArcherKit(Player p){
        PlayerInventory inv = p.getInventory();
        p.getActivePotionEffects().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(20);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setGameMode(GameMode.SURVIVAL);
        ItemStack healing = new ItemStack(Material.POTION,1,(short)16421);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET,1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        helmet.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE,1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        chestplate.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS,1);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        leggings.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS,1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        boots.addEnchantment(Enchantment.DURABILITY,3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL,4);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL,1);
        sword.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack bow = new ItemStack(Material.BOW,1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE,4);
        bow.addEnchantment(Enchantment.DURABILITY,3);
        bow.addEnchantment(Enchantment.ARROW_INFINITE,1);
        bow.addEnchantment(Enchantment.ARROW_FIRE,1);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0,sword);
        inv.setItem(1,bow);
        inv.setItem(9, new ItemStack(Material.SUGAR,64));
        inv.setItem(18, new ItemStack(Material.FEATHER,64));
        inv.setItem(2, new ItemStack(Material.ENDER_PEARL,16));
        inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));
        inv.setItem(27, new ItemStack(Material.ARROW,64));
        for(int i = 0; i < 34; i++){
            inv.addItem(healing);
        }
        p.updateInventory();
    }

    public static void giveRogueKit(Player p){
        PlayerInventory inv = p.getInventory();
        p.getActivePotionEffects().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(20);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setGameMode(GameMode.SURVIVAL);
        ItemStack healing = new ItemStack(Material.POTION,1,(short)16421);
        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET,1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        helmet.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE,1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        chestplate.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS,1);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        leggings.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS,1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        boots.addEnchantment(Enchantment.DURABILITY,3);
        boots.addEnchantment(Enchantment.PROTECTION_FALL,4);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD,1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL,1);
        sword.addEnchantment(Enchantment.DURABILITY,3);
        ItemStack gsword = new ItemStack(Material.GOLD_SWORD,1);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0,sword);
        inv.setItem(1, new ItemStack(Material.SUGAR,64));
        inv.setItem(2,gsword);
        inv.setItem(3, new ItemStack(Material.ENDER_PEARL,16));
        inv.setItem(8, new ItemStack(Material.COOKED_BEEF,64));
        inv.setItem(9,gsword);
        inv.setItem(10, gsword);
        inv.setItem(18, gsword);
        inv.setItem(19,gsword);
        inv.setItem(27,gsword);
        inv.setItem(28, gsword);
        for (int i = 0; i < 34 ; i++) {
            inv.addItem(healing);
        }
        p.updateInventory();
    }

    public static void giveBuilderKit(Player p){
        PlayerInventory inv = p.getInventory();
        p.getActivePotionEffects().clear();
        inv.clear();
        inv.setArmorContents(null);
        p.setHealth(20.0);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20.0f);
        p.setGameMode(GameMode.SURVIVAL);
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        axe.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack spade = new ItemStack(Material.DIAMOND_SPADE);
        spade.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
        pick.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack healing = new ItemStack(Material.POTION, 1, (short)16421);
        inv.setHelmet(helmet);
        inv.setChestplate(chest);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0, sword);
        inv.setItem(8, new ItemStack(Material.COOKED_BEEF, 64));
        inv.addItem(new ItemStack(axe));
        inv.addItem(new ItemStack(spade));
        inv.addItem(new ItemStack(pick));
        inv.addItem(new ItemStack(Material.ANVIL, 2));
        inv.addItem(new ItemStack(Material.CHEST, 64));
        inv.addItem(new ItemStack(Material.WORKBENCH, 2));
        inv.addItem(new ItemStack(Material.WATER_BUCKET, 1));
        inv.addItem(new ItemStack(Material.COBBLESTONE, 64));
        inv.addItem(new ItemStack(Material.COBBLESTONE, 64));
        inv.addItem(new ItemStack(Material.COBBLESTONE, 64));
        inv.addItem(new ItemStack(Material.COBBLESTONE, 64));
        inv.addItem(new ItemStack(Material.MYCEL, 64));
        inv.addItem(new ItemStack(Material.NETHER_BRICK, 64));
        inv.addItem(new ItemStack(Material.GLOWSTONE, 64));
        inv.addItem(new ItemStack(Material.LOG, 64));
        inv.addItem(new ItemStack(Material.LOG_2, 64));
        inv.addItem(new ItemStack(Material.STONE, 64));
        inv.addItem(new ItemStack(Material.STONE, 64));
        inv.addItem(new ItemStack(Material.STONE, 64));
        inv.addItem(new ItemStack(Material.STONE, 64));
        inv.addItem(new ItemStack(Material.STRING, 64));
        inv.addItem(new ItemStack(Material.STRING, 64));
        inv.addItem(new ItemStack(Material.GLASS, 64));
        inv.addItem(new ItemStack(Material.GLASS, 64));
        inv.addItem(new ItemStack(Material.STONE_PLATE, 64));
        inv.addItem(new ItemStack(Material.REDSTONE_BLOCK, 64));
        inv.addItem(new ItemStack(Material.REDSTONE, 64));
        inv.addItem(new ItemStack(Material.PISTON_BASE, 64));
        inv.addItem(new ItemStack(Material.PISTON_STICKY_BASE, 64));
        inv.addItem(new ItemStack(Material.REDSTONE_COMPARATOR, 64));
        inv.addItem(new ItemStack(Material.DIODE, 64));
        inv.addItem(new ItemStack(Material.REDSTONE_TORCH_OFF, 64));
        inv.addItem(new ItemStack(Material.STONE_BUTTON, 64));
        for(int i = 0; i < 34; i++){
            inv.addItem(healing);
        }
        p.updateInventory();
    }

    public static void giveMinerKit(Player p){
        PlayerInventory inv = p.getInventory();
        p.getActivePotionEffects().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(20);
        p.setFireTicks(0);
        p.setFoodLevel(20);
        p.setSaturation(20f);
        p.setGameMode(GameMode.SURVIVAL);
        ItemStack healing = new ItemStack(Material.POTION,1,(short)16421);
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        axe.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack spade = new ItemStack(Material.DIAMOND_SPADE);
        spade.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
        pick.addEnchantment(Enchantment.DIG_SPEED, 5);
        axe.addEnchantment(Enchantment.DURABILITY, 3);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItem(0,sword);
        inv.setItem(1, pick);
        inv.setItem(2, axe);
        inv.setItem(3, spade);
        inv.setItem(4, new ItemStack(Material.ENDER_PEARL,16));
        inv.setItem(5, new ItemStack(Material.COOKED_BEEF,64));
        inv.setItem(6, new ItemStack(Material.WATER_BUCKET,1));
        inv.setItem(7, new ItemStack(Material.ANVIL,8));
        for(int i = 0; i < 34; i++){
            inv.addItem(healing);
        }
        p.updateInventory();
    }

}
