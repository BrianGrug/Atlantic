package me.scifi.hcf.listener;


import me.scifi.hcf.HCF;
import me.scifi.hcf.command.CobbleCommand;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CobbleListener implements Listener {

    private HCF plugin;

    public CobbleListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(CobbleCommand.players.contains(p.getUniqueId()) && e.getBlock().getType() == Material.COBBLESTONE){
            e.getBlock().setType(Material.AIR);
            e.getBlock().getState().update();
        } else if(CobbleCommand.players.contains(p.getUniqueId()) && e.getBlock().getType() == Material.STONE){
            e.getBlock().setType(Material.AIR);
            e.getBlock().getState().update();
        }
    }

    @EventHandler
    public void onAutoSmelt(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(p.hasPermission("hcf.utilities.autosmelt")){
           if(p.getInventory().getItemInHand() != null &&!p.getInventory().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)){
               if(e.getBlock().getType() == Material.IRON_ORE){
                   ItemStack dropedItem = new ItemStack(Material.IRON_INGOT,1);
                   p.getWorld().dropItemNaturally(e.getBlock().getLocation(),dropedItem);
                   e.getBlock().setType(Material.AIR);
                   e.getBlock().getState().update();
               } else if(e.getBlock().getType() == Material.GOLD_ORE){
                   ItemStack dropedItem = new ItemStack(Material.GOLD_INGOT,1);
                   p.getWorld().dropItemNaturally(e.getBlock().getLocation(),dropedItem);
                   e.getBlock().setType(Material.AIR);
                   e.getBlock().getState().update();
               }
           }
        }
    }

}
