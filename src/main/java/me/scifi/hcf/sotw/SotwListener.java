package me.scifi.hcf.sotw;

import me.scifi.hcf.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import me.scifi.hcf.HCF;

public class SotwListener implements Listener {

    private HCF plugin;

    public SotwListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        SotwTimer.SotwRunnable sotwRunnable = plugin.getSotwTimer().getSotwRunnable();
        if (sotwRunnable != null) {
            if(e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if(!SotwCommand.enabled.contains(p.getUniqueId())){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        SotwTimer.SotwRunnable sotwRunnable = plugin.getSotwTimer().getSotwRunnable();
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player damaged = (Player) e.getEntity();
            if (sotwRunnable != null) {
                if (SotwCommand.enabled.contains(damager.getUniqueId()) && !SotwCommand.enabled.contains(damaged.getUniqueId())) {
                    damager.sendMessage(Utils.chat(plugin.messagesYML.getString("SOTW-DAMAGED-NOT-ENABLED")));
                    e.setCancelled(true);
                } else if (!SotwCommand.enabled.contains(damager.getUniqueId()) && SotwCommand.enabled.contains(damaged.getUniqueId())) {
                    damager.sendMessage(Utils.chat(plugin.messagesYML.getString("SOTW-DAMAGER-NOT-ENABLED")));
                    e.setCancelled(true);
                } else if (!SotwCommand.enabled.contains(damager.getUniqueId()) && !SotwCommand.enabled.contains(damaged.getUniqueId())) {
                    damager.sendMessage(Utils.chat(plugin.messagesYML.getString("SOTW-DAMAGER-NOT-ENABLED")));
                    e.setCancelled(true);
                }

            }
        }
    }
}

