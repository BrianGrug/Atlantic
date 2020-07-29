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
        if(plugin.getManagerHandler().getKingManager().isEventActive()){
            if(plugin.getManagerHandler().getKingManager().getKingPlayer().equals(p)){
               plugin.getManagerHandler().getKingManager().removeKing(true);
               List<String> killedMessage = plugin.getMessagesYML().getStringList("KING-DEATH");
               killedMessage.forEach(str -> Bukkit.broadcastMessage(Utils.chat(str)));

               if(p.getKiller() != null) {
                   if(plugin.getManagerHandler().getFactionManager().getPlayerFaction(p.getKiller()) != null){
                       plugin.getManagerHandler().getFactionManager().getPlayerFaction(p.getKiller()).addPoints(plugin.getConfig().getLong("ktk-points"));
                   }
               }
            }
        }
    }

}
