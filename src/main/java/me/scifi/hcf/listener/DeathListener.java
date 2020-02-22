package me.scifi.hcf.listener;

import com.doctordark.util.JavaUtils;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.struct.Role;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.user.FactionUser;
import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeathListener implements Listener {

    private final HCF plugin;

    public static Map<UUID, ItemStack[]> armor = new HashMap<>();
    public static Map<UUID, ItemStack[]> contents = new HashMap<>();

    public DeathListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeathKillIncrement(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            FactionUser user = plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
        }
    }

    private static final long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if(event.getEntity().getKiller() != null){
            if(plugin.getFactionManager().getPlayerFaction(event.getEntity().getKiller()) != null){
                plugin.getFactionManager().getPlayerFaction(event.getEntity().getKiller()).addPoints(1);
            }
        }

        if(!armor.containsKey(player.getUniqueId()) && !contents.containsKey(player.getUniqueId())) {
            armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            contents.put(player.getUniqueId(), player.getInventory().getContents());
        }
            armor.remove(player.getUniqueId());
            contents.remove(player.getUniqueId());
            armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            contents.put(player.getUniqueId(), player.getInventory().getContents());

        if (playerFaction != null) {
            playerFaction.setPoints(playerFaction.getPoints() - 1);
            if(plugin.getConfig().getBoolean("kit-map")){
                Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
                double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getMaximumDeathsUntilRaidable());

                Role role = playerFaction.getMember(player.getUniqueId()).getRole();
                playerFaction.setRemainingRegenerationTime(TimeUnit.MINUTES.toMillis(1L));
                playerFaction.broadcast(ChatColor.GOLD + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.GOLD + ". " + "DTR: (" + ChatColor.WHITE
                        + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GOLD + ").");
            } else {
                Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
                double dtrLoss = (1.0D * factionAt.getDtrLossMultiplier());
                double newDtr = playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

                Role role = playerFaction.getMember(player.getUniqueId()).getRole();
                playerFaction.setRemainingRegenerationTime(TimeUnit.SECONDS.toMillis(plugin.getConfig().getLong("HCF-DTR-REGEN-TIME")));
                playerFaction.broadcast(ChatColor.GOLD + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.GOLD + ". " + "DTR: (" + ChatColor.WHITE
                        + JavaUtils.format(newDtr, 2) + '/' + JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GOLD + ").");
               }
            }

        if (Bukkit.spigot().getTPS()[0] > 15) { // Prevent unnecessary lag during prime times.
            Location location = player.getLocation();
            WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

            EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(), location.getZ(), false);
            PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
                    ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
                }
            }
        }
    }
}
