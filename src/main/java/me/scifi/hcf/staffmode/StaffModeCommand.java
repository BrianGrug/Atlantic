package me.scifi.hcf.staffmode;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StaffModeCommand implements CommandExecutor {

    private HCF plugin;

    public static Set<UUID> staffMode = new HashSet<>();

    public static Map<UUID, ItemStack[]> armorContents = new HashMap<>();

    public static Map<UUID, ItemStack[]> inventoryContents = new HashMap<>();

    public StaffModeCommand(HCF plugin){
        this.plugin = plugin;
    }


    public static void putInStaffMode(Player p){
        p.setGameMode(GameMode.CREATIVE);
        staffMode.add(p.getUniqueId());
        armorContents.put(p.getUniqueId(),p.getInventory().getArmorContents());
        inventoryContents.put(p.getUniqueId(),p.getInventory().getContents());
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        StaffModeItems.giveModItems(p);
        p.setHealth(20);
        p.setFoodLevel(20);
    Vanish.setVanished(p);
    }

    public static void removeFromStaffMode(Player p){
        staffMode.remove(p.getUniqueId());
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.getInventory().setArmorContents(armorContents.get(p.getUniqueId()));
        p.getInventory().setContents(inventoryContents.get(p.getUniqueId()));
        armorContents.remove(p.getUniqueId());
        inventoryContents.remove(p.getUniqueId());
        Vanish.disableVanish(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("staffmode")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(command.getPermission())){
                   if(staffMode.contains(p.getUniqueId())){
                       removeFromStaffMode(p);
                       p.sendMessage(Utils.chat(plugin.messagesYML.getString("STAFFMODE-DISABLED")));
                       return true;
                   } else {
                       putInStaffMode(p);
                       p.sendMessage(Utils.chat(plugin.messagesYML.getString("STAFFMODE-ENABLED")));
                       return true;
                   }
                }
            }
        }
        return false;
    }
}
