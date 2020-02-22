package me.scifi.hcf.listener;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.command.FreezeCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class FreezeListener implements Listener {

    private HCF plugin;

    public FreezeListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(FreezeCommand.frozen.containsKey(p.getUniqueId())){
            if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()){
                p.teleport(FreezeCommand.frozen.get(p.getUniqueId()));
                List<String> frozen_msg = plugin.messagesYML.getStringList("PLAYER-FROZEN-MESSAGE");
                Utils.list(frozen_msg).forEach(p::sendMessage);
            }
        }
    }
    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(FreezeCommand.frozen.containsKey(p.getUniqueId())){
            if(plugin.getConfig().getBoolean("FROZEN-LOGOUT-AUTOBAN")){
                FreezeCommand.frozen.remove(p.getUniqueId());
                for(Player staff : Bukkit.getServer().getOnlinePlayers()){
                    if(staff.hasPermission("hcf.command.freeze")){
                        staff.sendMessage(Utils.chat(plugin.messagesYML.getString("FROZEN-LOGOUT-MESSAGE")
                        .replace("%player%",p.getName())));
                    }
                }
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), plugin.getConfig().getString("FROZEN-LOGOUT-COMMAND")
                .replace("%player%",p.getName()));
            }
            FreezeCommand.frozen.remove(p.getUniqueId());
            for(Player staff : Bukkit.getServer().getOnlinePlayers()){
                if(staff.hasPermission("hcf.command.freeze")){
                    staff.sendMessage(Utils.chat(plugin.messagesYML.getString("FROZEN-LOGOUT-MESSAGE")
                            .replace("%player%",p.getName())));
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
       if(e.getEntity() instanceof Player){
           Player p = (Player) e.getEntity();
           if(FreezeCommand.frozen.containsKey(p.getUniqueId())){
               e.setCancelled(true);
           }
       }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
            Player damager = (Player) e.getDamager();
            Player damaged = (Player) e.getEntity();
            if(FreezeCommand.frozen.containsKey(damaged.getUniqueId())){
                damager.sendMessage(Utils.chat(plugin.messagesYML.getString("HIT-FROZEN-PLAYER")
                .replace("%player%",damaged.getName())));
                e.setCancelled(true);
            }

            if(FreezeCommand.frozen.containsKey(damager.getUniqueId())){
                damager.sendMessage(Utils.chat(plugin.messagesYML.getString("HIT-WHILE-FROZEN")));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(FreezeCommand.frozen.containsKey(p.getUniqueId())){
            p.sendMessage(Utils.chat(plugin.messagesYML.getString("FROZEN-BLOCK-PLACE")));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(FreezeCommand.frozen.containsKey(p.getUniqueId())){
            p.sendMessage(Utils.chat(plugin.messagesYML.getString("FROZEN-BLOCK-BREAK")));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e){
        Player p = e.getPlayer();
        if(e.getMessage().startsWith("/") && FreezeCommand.frozen.containsKey(p.getUniqueId())){
            p.sendMessage(Utils.chat(plugin.messagesYML.getString("FROZEN-COMMAND-USE")));
            e.setCancelled(true);
        }
    }

}
