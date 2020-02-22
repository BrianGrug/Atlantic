package me.scifi.hcf.staffmode;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StaffModeItems {

    public static void giveModItems(Player p){
        ItemStack book = new ItemStack(Material.BOOK);
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemStack ice = new ItemStack(Material.ICE);
        ItemStack carpet = new ItemStack(Material.CARPET);
        ItemStack dye = new ItemStack(Material.INK_SACK, 1 , (byte) 10);
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);

        ItemMeta bookMeta = book.getItemMeta();
        ItemMeta compassMeta = compass.getItemMeta();
        ItemMeta iceMeta = ice.getItemMeta();
        ItemMeta carpetMeta = carpet.getItemMeta();
        ItemMeta dyeMeta = dye.getItemMeta();
        ItemMeta headMeta = head.getItemMeta();

        bookMeta.setDisplayName(Utils.chat(HCF.getPlugin().messagesYML.getString("STAFFMODE.INSPECT.NAME")));
        bookMeta.setLore(Utils.list(HCF.getPlugin().messagesYML.getStringList("STAFFMODE.INSPECT.LORE")));

        compassMeta.setDisplayName(Utils.chat(HCF.getPlugin().messagesYML.getString("STAFFMODE.THRUCOMPASS.NAME")));
        compassMeta.setLore(Utils.list(HCF.getPlugin().messagesYML.getStringList("STAFFMODE.THRUCOMPASS.LORE")));


        iceMeta.setDisplayName(Utils.chat(HCF.getPlugin().messagesYML.getString("STAFFMODE.FREEZEBLOCK.NAME")));
        iceMeta.setLore(Utils.list(HCF.getPlugin().messagesYML.getStringList("STAFFMODE.FREEZEBLOCK.LORE")));

        carpetMeta.setDisplayName(Utils.chat(HCF.getPlugin().messagesYML.getString("STAFFMODE.BETTERVIEW.NAME")));
        carpetMeta.setLore(Utils.list(HCF.getPlugin().messagesYML.getStringList("STAFFMODE.BETTERVIEW.LORE")));

        dyeMeta.setDisplayName(Utils.chat(HCF.getPlugin().messagesYML.getString("STAFFMODE.VANISHITEM.NAME.ENABLED")));
        dyeMeta.setLore(Utils.list(HCF.getPlugin().messagesYML.getStringList("STAFFMODE.VANISHITEM.LORE")));

        headMeta.setDisplayName(Utils.chat(HCF.getPlugin().messagesYML.getString("STAFFMODE.RANDOMTP.NAME")));
        headMeta.setLore(Utils.list(HCF.getPlugin().messagesYML.getStringList("STAFFMODE-RANDOMTP.LORE")));

        book.setItemMeta(bookMeta);
        compass.setItemMeta(compassMeta);
        ice.setItemMeta(iceMeta);
        carpet.setItemMeta(carpetMeta);
        dye.setItemMeta(dyeMeta);
        head.setItemMeta(headMeta);

        PlayerInventory inv = p.getInventory();
        inv.setItem(HCF.getPlugin().messagesYML.getInt("STAFFMODE.INSPECT.SLOT"),book);
        inv.setItem(HCF.getPlugin().messagesYML.getInt("STAFFMODE.THRUCOMPASS.SLOT"),compass);
        inv.setItem(HCF.getPlugin().messagesYML.getInt("STAFFMODE.FREEZEBLOCK.SLOT"),ice);
        inv.setItem(HCF.getPlugin().messagesYML.getInt("STAFFMODE.BETTERVIEW.SLOT"),carpet);
        inv.setItem(HCF.getPlugin().messagesYML.getInt("STAFFMODE.VANISHITEM.SLOT"),dye);
        inv.setItem(HCF.getPlugin().messagesYML.getInt("STAFFMODE.RANDOMTP.SLOT"), head);
    }

}
