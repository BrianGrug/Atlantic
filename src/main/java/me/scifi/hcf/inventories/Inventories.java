package me.scifi.hcf.inventories;

import com.doctordark.util.ItemBuilder;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class Inventories {
    private static HCF plugin = HCF.getPlugin();
    public static void staffInventoryInspector(Player p, Player target) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&cInspecting: " + target.getName()));
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                List<String> potionEffectsList = new ArrayList<>();
                PlayerInventory playerInv = target.getInventory();
                p.getActivePotionEffects().forEach(name -> potionEffectsList.add(name.toString().toUpperCase()));
                ItemStack foodLevel = new ItemStack(Material.COOKED_BEEF,1);
                ItemMeta foodLevelMeta = foodLevel.getItemMeta();
                foodLevelMeta.setDisplayName(Utils.chat("&eFood Level"));
                List<String> foodLevelLore = new ArrayList<>();
                foodLevelLore.add(Utils.chat("&f" + target.getFoodLevel()));
                foodLevelMeta.setLore(foodLevelLore);
                foodLevel.setItemMeta(foodLevelMeta);
                ItemStack potionEffects = new ItemStack(Material.BREWING_STAND_ITEM,1);
                ItemMeta potionEffectsMeta = potionEffects.getItemMeta();
                potionEffectsMeta.setDisplayName(Utils.chat("&ePotion Effects"));
                for (PotionEffect potionEffect : target.getActivePotionEffects()){
                    int effectLevel = potionEffect.getAmplifier();
                    effectLevel++;
                    potionEffectsList.add(Utils.chat("&e" + WordUtils.capitalizeFully(potionEffect.getType().getName()).replace("_", " ") + " " + effectLevel));
                }
                potionEffectsMeta.setLore(potionEffectsList);
                potionEffects.setItemMeta(potionEffectsMeta);
                inventory.setContents(playerInv.getContents());
                inventory.setItem(36, playerInv.getHelmet());
                inventory.setItem(37, playerInv.getChestplate());
                inventory.setItem(38, playerInv.getLeggings());
                inventory.setItem(39, playerInv.getBoots());
                inventory.setItem(40, playerInv.getItemInHand());
                inventory.setItem(42, foodLevel);
                inventory.setItem(43, potionEffects);
            }
        },0,5);
        p.openInventory(inventory);
    }

    public static void playerInspectInventory(Player p, Player target){
        Inventory inventory = Bukkit.getServer().createInventory(null,36,Utils.chat("&eViewing " + target.getName()));
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> inventory.setContents(target.getInventory().getContents()),0,5);
        p.openInventory(inventory);
    }
    public static void punishFactionInventory(Player player, PlayerFaction playerFaction){
        Inventory inventory = Bukkit.getServer().createInventory(null, 54, Utils.chat("&cPunish faction " + playerFaction.getName()));
        inventory.setItem(13, new ItemBuilder(Material.ANVIL).lore(ChatColor.YELLOW.toString() + playerFaction.getMembers().size() + " members", " " , ChatColor.GRAY + "Click to see members").displayName(ChatColor.RED + "Faction: " + "'" +playerFaction.getName() + "'").build());

        inventory.setItem(20, new ItemBuilder(Material.WOOL).data(DyeColor.YELLOW.getData()).displayName(ChatColor.RED + "Tempmute faction for 1h").build());
        inventory.setItem(29, new ItemBuilder(Material.WOOL).data(DyeColor.ORANGE.getData()).displayName(ChatColor.RED + "Tempmute faction for 24h").build());
        inventory.setItem(38, new ItemBuilder(Material.WOOL).data(DyeColor.RED.getData()).displayName(ChatColor.RED + "Mute faction permanently").build());

        inventory.setItem(22, new ItemBuilder(Material.WOOL).data(DyeColor.YELLOW.getData()).displayName(ChatColor.RED + "Warn faction").build());
        inventory.setItem(31, new ItemBuilder(Material.WOOL).data(DyeColor.ORANGE.getData()).displayName(ChatColor.RED + "Kick faction").build());

        inventory.setItem(24, new ItemBuilder(Material.WOOL).data(DyeColor.YELLOW.getData()).displayName(ChatColor.RED + "Ban faction for 1h").build());
        inventory.setItem(33, new ItemBuilder(Material.WOOL).data(DyeColor.ORANGE.getData()).displayName(ChatColor.RED + "Ban faction for 24h").build());
        inventory.setItem(42, new ItemBuilder(Material.WOOL).data(DyeColor.RED.getData()).displayName(ChatColor.RED + "Ban faction permanently").build());

        player.openInventory(inventory);
    }
    public static void factionMembers(Player player, PlayerFaction playerFaction){
        Inventory inventory = Bukkit.getServer().createInventory(null, 54, Utils.chat("&cPunish faction " + playerFaction.getName()));

        playerFaction.getOnlinePlayers().forEach(member -> {
            inventory.addItem(new ItemBuilder(Material.SKULL_ITEM).displayName(ChatColor.YELLOW + member.getName()).toSkullBuilder().withOwner(member.getDisplayName()).buildSkull());

            inventory.setItem(53, new ItemBuilder(Material.REDSTONE_BLOCK).displayName(ChatColor.RED + "Go back").build());
            player.openInventory(inventory);
        });
    }
}
