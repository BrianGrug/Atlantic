package me.scifi.hcf.ktk;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class KingListener implements Listener {

    private HCF plugin;

    public KingListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onKingDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(plugin.kingManager.isEventActive()){
            if(plugin.kingManager.getKingPlayer().equals(p)){
               plugin.kingManager.removeKing(true);
               List<String> killedMessage = plugin.messagesYML.getStringList("KING-DEATH");
               killedMessage.forEach(str -> Bukkit.broadcastMessage(Utils.chat(str)));

               if(p.getKiller() != null) {
                   if(plugin.getFactionManager().getPlayerFaction(p.getKiller()) != null){
                       plugin.getFactionManager().getPlayerFaction(p.getKiller()).addPoints(plugin.getConfig().getLong("ktk-points"));
                   }
               }
            }
        }
    }

}
