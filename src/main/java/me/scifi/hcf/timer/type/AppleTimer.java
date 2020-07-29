package me.scifi.hcf.timer.type;

import javafx.beans.property.adapter.JavaBeanProperty;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.timer.PlayerTimer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class AppleTimer extends PlayerTimer implements Listener {

    public AppleTimer(HCF plugin) {
        super(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.APPLE.NAME"), TimeUnit.SECONDS.toMillis(HCF.getPlugin().getMessagesYML().getLong("SCOREBOARD.APPLE.LENGTH")));
    }

    @Override
    public String getScoreboardPrefix() {
        return HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.APPLE.PREFIX");
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e){
        Player p = e.getPlayer();
        if(e.getItem() != null  && e.getItem().getType() == Material.GOLDEN_APPLE && e.getItem().getDurability() == 0){
           if(getRemaining(p) <= 0){
               setCooldown(p, p.getUniqueId(), this.defaultCooldown, false);
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588\u2588&c\u2588\u2588\u2588"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588&c\u2588\u2588\u2588\u2588 &6&l " + this.name + ": "));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588 &7  Consumed"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&f\u2588&6\u2588&6\u2588\u2588&c\u2588"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588&f\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &6 Cooldown Remaining:"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &7  " + DurationFormatter.getRemaining(this.defaultCooldown, true, true)));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
           } else {
               p.sendMessage(Utils.chat(ChatColor.RED + "You still have a " + getDisplayName() + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + DurationFormatter.getRemaining(getRemaining(p), true, false) + ChatColor.RED + '.'));
               e.setCancelled(true);
           }
        }
    }
}
