package me.scifi.hcf.staffmode;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Vanish {

    public static Set<UUID> vanishedPlayers = new HashSet<>();

    public static boolean isPlayerVanished(Player p){
        return vanishedPlayers.contains(p.getUniqueId());
    }

    public static void setVanished(Player p){
        vanishedPlayers.add(p.getUniqueId());
        for(Player players : Bukkit.getServer().getOnlinePlayers()){
            if(!players.hasPermission("hcf.command.vanish")) {
                players.hidePlayer(p);
            }
        }
    }

    public static void disableVanish(Player p){
        vanishedPlayers.remove(p.getUniqueId());
        for(Player players : Bukkit.getServer().getOnlinePlayers()){
                players.showPlayer(p);

        }
    }

}
