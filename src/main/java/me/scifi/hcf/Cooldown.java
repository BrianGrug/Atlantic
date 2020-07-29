package me.scifi.hcf;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Cooldown {

    public Map<UUID,Long> cooldownMap = new HashMap<>();


    public boolean isOnCooldown(Player p){
        return cooldownMap.containsKey(p.getUniqueId()) && cooldownMap.get(p.getUniqueId()) >= System.currentTimeMillis();
    }

    public long getRemaining(Player p){
        return cooldownMap.get(p.getUniqueId()) - System.currentTimeMillis();
    }

    public void placeOnCooldown(Player p, long duration){
        if(!isOnCooldown(p)){
            cooldownMap.put(p.getUniqueId(), System.currentTimeMillis() + duration * 1000);
        }
    }

}
