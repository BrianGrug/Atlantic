package me.scifi.hcf.listener;

import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.inventories.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PunishListener implements Listener {


    @EventHandler
    public void onPunish(InventoryClickEvent event) {
        if (event.getInventory() != null) {
            if (event.getInventory().getName().startsWith(ChatColor.RED + "Punish faction ")) {
                event.setCancelled(true);
                String name = event.getInventory().getName();
                String[] f = name.split(" ");

                Faction faction = HCF.getPlugin().getManagerHandler().getFactionManager().getFaction(f[f.length -1]);
                PlayerFaction playerFaction = (PlayerFaction) faction;

                if(event.getCurrentItem() == null){
                    return;
                }
                if(event.getCurrentItem().getItemMeta() == null){
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Faction: " + "'" +playerFaction.getName() + "'")){
                    Inventories.factionMembers((Player) event.getWhoClicked(), playerFaction);
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Go back")){
                    Inventories.punishFactionInventory((Player) event.getWhoClicked(), playerFaction);
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Tempmute faction for 1h")) {
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempmute " + player.getName() + " 1h Faction mute -s"));
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Tempmute faction for 24h")) {
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempmute " + player.getName() + " 24h Faction mute -s"));
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Mute faction permanently")) {
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + player.getName() + " Faction mute -s"));
                    return;
                }

                if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Warn faction")){
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warn " + player.getName() + " Faction warn -s"));
                    return;
                }
                if(event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Kick faction")){
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + player.getName() + " Faction kick -s"));
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Ban faction for 1h")) {
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + player.getName() + " 1h Faction ban -s"));
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Ban faction for 24h")) {
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + player.getName() + " 24h Faction ban -s"));
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Ban faction permanently")) {
                    playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " Faction ban -s"));
                }
            }
        }
    }
}