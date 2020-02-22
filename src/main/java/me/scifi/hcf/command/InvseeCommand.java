package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.inventories.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvseeCommand implements CommandExecutor {

    private HCF plugin;

    public static Set<String> inventorys = new HashSet<>();

    public InvseeCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Utils.chat("&cOnly players may execute this command."));
            return true;
        }

        Player p = (Player) sender;

        if(p.hasPermission(command.getPermission())){

            if(args.length < 1){
                p.sendMessage(Utils.chat("&cUsage: /invsee <player>"));
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[0]);

            if(target == null){
                p.sendMessage(Utils.chat("&cPlayer is not online."));
                return true;
            }

            Inventory inv = Bukkit.getServer().createInventory(null,36,Utils.chat("&eInspecting: &f" + target.getName()));
           // Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> inv.setContents(target.getInventory().getContents()),0,2);
            inventorys.add(target.getName());
            p.openInventory(inv);

        }
        return false;
        }
    }

