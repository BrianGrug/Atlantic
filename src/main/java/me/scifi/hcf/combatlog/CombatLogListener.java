package me.scifi.hcf.combatlog;

//import com.connorl.istaff.ISDataBaseManager;

import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.combatlog.event.LoggerRemovedEvent;
import me.scifi.hcf.combatlog.event.LoggerSpawnEvent;
import me.scifi.hcf.combatlog.type.LoggerEntity;
import me.scifi.hcf.combatlog.type.LoggerEntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.gmail.xd.zwander.istaff.data.PlayerHackerMode;

/**
 * Listener that prevents {@link Player}s from combat-logging.
 */
public class CombatLogListener implements Listener {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private static final int NEARBY_SPAWN_RADIUS = 64;

    private final Set<UUID> safelyDisconnected = new HashSet<>();
    private final Map<UUID, LoggerEntity> loggers = new HashMap<>();

    private final HCF plugin;

    public CombatLogListener(HCF plugin) {
        this.plugin = plugin;
    }

    /**
     * Disconnects a {@link Player} without a {@link LoggerEntity} spawning.
     *
     * @param player the {@link Player} to disconnect
     * @param reason the reason for disconnecting
     */
    public void safelyDisconnect(Player player, String reason) {
        if (safelyDisconnected.add(player.getUniqueId())) {
            player.kickPlayer(reason);
        }
    }

    /**
     * Removes all the {@link LoggerEntity} instances from the server.
     */
    public void removeCombatLoggers() {
        Iterator<LoggerEntity> iterator = loggers.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
            iterator.remove();
        }

        safelyDisconnected.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLoggerRemoved(LoggerRemovedEvent event) {
        loggers.remove(event.getLoggerEntity().getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        LoggerEntity currentLogger = loggers.remove(event.getPlayer().getUniqueId());
        if (currentLogger != null) {
            currentLogger.destroy();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean result = safelyDisconnected.remove(uuid);
        if (!ConfigurationService.COMBAT_LOG_PREVENTION_ENABLED) {
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE && !player.isDead() && !result) {
            // If the player has PVP protection, don't spawn a logger
            if (plugin.getManagerHandler().getTimerManager().getPvpTimer().getRemaining(uuid) > 0L)
                return;


            // There is no enemies near the player, so don't spawn a logger.
            if (plugin.getManagerHandler().getTimerManager().getTeleportTimer().getNearbyEnemies(player, NEARBY_SPAWN_RADIUS) <= 0 || plugin.getSotwTimer().getSotwRunnable() != null)
                return;


            // Make sure the player is not in a safezone.
            Location location = player.getLocation();
            if (plugin.getManagerHandler().getFactionManager().getFactionAt(location).isSafezone())
                return;


            // Make sure the player hasn't already spawned a logger.
            if (loggers.containsKey(player.getUniqueId()))
                return;


            LoggerEntity loggerEntity = new LoggerEntityHuman(player, location.getWorld());
            LoggerSpawnEvent calledEvent = new LoggerSpawnEvent(loggerEntity);
            Bukkit.getPluginManager().callEvent(calledEvent);
            if (!calledEvent.isCancelled()) {
                loggers.put(player.getUniqueId(), loggerEntity);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) { // just in-case
                            loggerEntity.postSpawn(plugin);
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }
}
