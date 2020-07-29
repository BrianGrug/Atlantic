package me.scifi.hcf.staffmode;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.inventories.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StaffModeListener implements Listener {

    private HCF plugin;

    public StaffModeListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE-BLOCK-PLACE")));
            e.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e){
        if(Vanish.vanishedPlayers.contains(e.getPlayer().getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE-BLOCK-BREAK")));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e){
        if(Vanish.vanishedPlayers.contains(e.getPlayer().getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
                e.setCancelled(true);
            }

            if(Vanish.isPlayerVanished(p)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            Player damager = (Player) e.getDamager();
                if(StaffModeCommand.staffMode.contains(damager.getUniqueId())){
                    e.setCancelled(true);

                if(Vanish.isPlayerVanished(damager)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
           if(StaffModeCommand.staffMode.contains(p.getUniqueId()) && e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta().getDisplayName().equals(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE.VANISHITEM.NAME.ENABLED")))){
               Vanish.disableVanish(p);
               ItemStack dye = new ItemStack(Material.INK_SACK,1,(short) 8);
               ItemMeta dyeMeta = dye.getItemMeta();
               dyeMeta.setDisplayName(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE.VANISHITEM.NAME.DISABLED")));
               List<String> lore = new ArrayList<>();
               for(String s : plugin.getMessagesYML().getStringList("STAFFMODE.VANISHITEM.LORE")){
                   lore.add(ChatColor.translateAlternateColorCodes('&',s));
                    }
               dyeMeta.setLore(lore);
               dye.setItemMeta(dyeMeta);
               p.getInventory().setItemInHand(dye);
                 } else if(StaffModeCommand.staffMode.contains(p.getUniqueId()) && e.getItem() != null  && e.getItem().getItemMeta().getDisplayName().equals(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE.VANISHITEM.NAME.DISABLED")))){
                Vanish.setVanished(p);
               ItemStack dye = new ItemStack(Material.INK_SACK,1,(short) 10);
               ItemMeta dyeMeta = dye.getItemMeta();
               dyeMeta.setDisplayName(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE.VANISHITEM.NAME.ENABLED")));
               List<String> lore = new ArrayList<>();
               for(String s : plugin.getMessagesYML().getStringList("STAFFMODE.VANISHITEM.LORE")){
                   lore.add(ChatColor.translateAlternateColorCodes('&',s));
               }
               dyeMeta.setLore(lore);
               dye.setItemMeta(dyeMeta);
               p.getInventory().setItemInHand(dye);
           }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteractTP(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack head = new ItemStack(Material.SKULL_ITEM,1);
        ItemMeta headMeta = head.getItemMeta();
        headMeta.setDisplayName(Utils.chat(HCF.getPlugin().getMessagesYML().getString("STAFFMODE.RANDOMTP.NAME")));
        headMeta.setLore(Utils.list(HCF.getPlugin().getMessagesYML().getStringList("STAFFMODE-RANDOMTP.LORE")));
        head.setItemMeta(headMeta);
        if(p.getItemInHand().equals(head)) {
            Random random = new Random();
            if (HCF.getOnlinePlayers().size() > 1) {
                int index = random.nextInt(HCF.getOnlinePlayers().size());
                Player to = (Player) Bukkit.getServer().getOnlinePlayers().toArray()[index];
                if (to != p) {
                    p.teleport(to);
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("TELEPORT-SUCCESSFUL-MESSAGE")));
                } else {
                    p.sendMessage(Utils.chat("&cYou cannot teleport to yourself."));
                }

            } else {
                p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("NOT-ENOUGH-PLAYERS")));
            }
        }
    }


    @EventHandler(priority =  EventPriority.MONITOR)
    public void onRightClick(PlayerInteractEntityEvent e){
        Player p = e.getPlayer();

        ItemMeta bookMeta = new ItemStack(Material.BOOK,1).getItemMeta();
        bookMeta.setDisplayName(Utils.chat(HCF.getPlugin().getMessagesYML().getString("STAFFMODE.INSPECT.NAME")));
        List<String> bookLore = new ArrayList<>();
        for (String s: HCF.getPlugin().getMessagesYML().getStringList("STAFFMODE.INSPECT.LORE")) {
            bookLore.add(ChatColor.translateAlternateColorCodes('&',s));
        }
        bookMeta.setLore(bookLore);

        if(e.getRightClicked() instanceof Player) {
                ItemStack ice = new ItemStack(Material.ICE);
                ItemMeta iceMeta = ice.getItemMeta();
                Player rightClicked = (Player) e.getRightClicked();
                iceMeta.setDisplayName(Utils.chat(HCF.getPlugin().getMessagesYML().getString("STAFFMODE.FREEZEBLOCK.NAME")));
                List<String> iceLore = new ArrayList<>();
                for (String s : HCF.getPlugin().getMessagesYML().getStringList("STAFFMODE.FREEZEBLOCK.LORE")) {
                    iceLore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                iceMeta.setLore(iceLore);
                ice.setItemMeta(iceMeta);
              if(p.getInventory().getItemInHand().getType() == Material.ICE){
                if (p.getInventory().getItemInHand().getItemMeta().equals(ice.getItemMeta())) {
                    if (StaffModeCommand.staffMode.contains(p.getUniqueId()) && p.hasPermission("hcf.command.freeze")) {
                        if (rightClicked != null) {
                            Bukkit.getServer().dispatchCommand(p, "freeze " + rightClicked.getName());
                        }
                    }
                }
            } else if(StaffModeCommand.staffMode.contains(p.getUniqueId()) && p.getInventory().getItemInHand().getType() == Material.BOOK && p.getInventory().getItemInHand().getItemMeta().equals(bookMeta)){
                  Inventories.staffInventoryInspector(p, rightClicked);
                  p.sendMessage(Utils.chat("&eNow Inspecting &f" + rightClicked.getName()));
              }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(PlayerPickupItemEvent e){
        Player p = e.getPlayer();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisconnect(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            StaffModeCommand.removeFromStaffMode(p);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().getTitle().startsWith(ChatColor.translateAlternateColorCodes('&', "&cInspecting: "))) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e){
        Player p = e.getPlayer();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("STAFFMODE-GAMEMODE-SWITCH")));
            p.setGameMode(GameMode.CREATIVE);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(StaffModeCommand.staffMode.contains(p.getUniqueId())){
            e.setCancelled(true);
        }
    }





}
