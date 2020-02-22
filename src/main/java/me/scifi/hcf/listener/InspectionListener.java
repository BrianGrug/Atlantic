package me.scifi.hcf.listener;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.command.InvseeCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class InspectionListener implements Listener {

    private HCF plugin = HCF.getPlugin();

    @EventHandler
    public void onClick(InventoryClickEvent e){

        Player p = (Player) e.getWhoClicked();

        if(e.getClickedInventory() == null) return;

        if(e.getCurrentItem() == null) return;

        if(!e.getClickedInventory().getName().startsWith(Utils.chat("&eInspecting:"))) return;

        if(!p.hasPermission("hcf.command.invsee.modify")) e.setCancelled(true);

        Player player = null;

        for(String name : InvseeCommand.inventorys){
            if(!e.getClickedInventory().getName().contains(Utils.chat("&f" + name))) continue;

            player = Bukkit.getServer().getPlayer(name);
        }

        if(player == null) e.setCancelled(true);

        else {
            player.getInventory().setContents(e.getClickedInventory().getContents());
            player.updateInventory();
        }

    }

}
