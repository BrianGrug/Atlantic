package me.scifi.hcf.reclaims;

import com.doctordark.util.Config;
import lombok.Data;
import me.scifi.hcf.HCF;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
public class ReclaimManager {

    public static Config reclaims = new Config(HCF.getPlugin(),"reclaims",HCF.getPlugin().getDataFolder().getAbsolutePath());

    public boolean hasReclaimed(Player p){
        return reclaims.get("reclaimed." + p.getUniqueId().toString()) != null;
    }

    public void setReclaimed(Player p){
            reclaims.set("reclaimed." + p.getUniqueId().toString(), UUID.randomUUID().toString());
            reclaims.save();
    }

    public void resetReclaims(Player p){
        reclaims.set("reclaimed." + p.getUniqueId(), null);
        reclaims.save();
    }

    public List<String> returnReclaim(String rank){
        return reclaims.getStringList("reclaims." + rank.toLowerCase() + ".commands");
    }

    public boolean hasReclaim(String rank){
        return(reclaims.get("reclaims." + rank.toLowerCase()) != null);
    }



}
