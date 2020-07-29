package me.scifi.hcf.listener;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoreListener implements Listener {

    private HCF plugin;

    protected Map<UUID,ItemStack[]> armor = new HashMap<>();
    protected Map<UUID,ItemStack[]> content = new HashMap<>();

    public CoreListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner
                && !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break spawners in the nether.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner
                && !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place spawners in the nether.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null) return;

        if(!e.getClickedInventory().getName().startsWith(Utils.chat("&cInspecting"))) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onCombatQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
        Player player = e.getPlayer();

       // if(plugin.getManagerHandler().getTimerManager().getCombatTimer().getRemaining(player) > 0) {
            //armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            //content.put(player.getUniqueId(), player.getInventory().getContents());
       // }
        //Arrays.asList(player.getInventory().getContents()).forEach(System.out::println);
        //Arrays.asList(player.getInventory().getArmorContents()).forEach(System.out::println);
    }

    @EventHandler
    public void onCombatJoin(PlayerJoinEvent e){
        e.setJoinMessage(null);
        Player player = e.getPlayer();

       // if(armor.containsKey(player.getUniqueId())){
           // player.getInventory().setArmorContents(armor.get(player.getUniqueId()));
            //armor.remove(player.getUniqueId());
            //player.getInventory().setContents(content.get(player.getUniqueId()));
            //content.remove(player.getUniqueId());
        //}
    }

    @EventHandler
    public void onRepair(PrepareAnvilRepairEvent e){
        ItemStack first = e.getFirst();
        ItemStack second = e.getSecond();
        ItemStack result = e.getResult();
        ItemMeta firstmeta = e.getFirst().getItemMeta();
        ItemMeta secondmeta = e.getSecond().getItemMeta();
        ItemMeta resultmeta = e.getResult().getItemMeta();
        Player p = (Player) e.getRepairer();
        if(firstmeta.getEnchantLevel(Enchantment.DAMAGE_ALL) == 1 && secondmeta.getEnchantLevel(Enchantment.DAMAGE_ALL) == 1){
            resultmeta.addEnchant(Enchantment.DAMAGE_ALL, 1,false);
        }

        if(firstmeta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) == 1 && secondmeta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) == 1){
            resultmeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCombatPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
      if(!plugin.getConfig().getBoolean("combat-place")) {
          if (plugin.getManagerHandler().getTimerManager().getCombatTimer().getRemaining(p) > 0) {
              p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("COMBAT-PLACE-DISABLED")));
              e.setCancelled(true);
          }
      }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent e){
        boolean isRaining = e.toWeatherState();
        if(isRaining){
            e.setCancelled(true);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        plugin.getManagerHandler().getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        plugin.getManagerHandler().getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        plugin.getManagerHandler().getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        plugin.getManagerHandler().getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

}
