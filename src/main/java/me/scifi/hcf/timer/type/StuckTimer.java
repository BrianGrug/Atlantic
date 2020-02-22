package me.scifi.hcf.timer.type;

import com.google.common.base.Predicate;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.LandMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.timer.PlayerTimer;
import me.scifi.hcf.timer.TimerCooldown;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StuckTimer extends PlayerTimer implements Listener {

    // The maximum distance a player can move before
    // this timer will self-cancel
    public static final int MAX_MOVE_DISTANCE = 5;

    private final Map<UUID, Location> startedLocations = new HashMap<>();

    public StuckTimer() {
        super(HCF.getPlugin().messagesYML.getString("SCOREBOARD.STUCK.NAME"), TimeUnit.SECONDS.toMillis(HCF.getPlugin().messagesYML.getLong("SCOREBOARD.STUCK.LENGTH")), false);
    }

    @Override
    public String getScoreboardPrefix() {
        return HCF.getPlugin().messagesYML.getString("SCOREBOARD.STUCK.PREFIX");
    }

    @Override
    public TimerCooldown clearCooldown(UUID uuid) {
        TimerCooldown runnable = super.clearCooldown(uuid);
        if (runnable != null) {
            this.startedLocations.remove(uuid);
            return runnable;
        }

        return null;
    }

    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long millis, boolean force, @Nullable Predicate<Long> callback) {
        if (player != null && super.setCooldown(player, playerUUID, millis, force, callback)) {
            this.startedLocations.put(playerUUID, player.getLocation());
            return true;
        }

        return false;
    }

    private void checkMovement(Player player, Location from, Location to) {
        UUID uuid = player.getUniqueId();
        if (getRemaining(uuid) > 0L) {
            if (from == null) {
                clearCooldown(uuid);
                return;
            }

            int xDiff = Math.abs(from.getBlockX() - to.getBlockX());
            int yDiff = Math.abs(from.getBlockY() - to.getBlockY());
            int zDiff = Math.abs(from.getBlockZ() - to.getBlockZ());
            if (xDiff > MAX_MOVE_DISTANCE || yDiff > MAX_MOVE_DISTANCE || zDiff > MAX_MOVE_DISTANCE) {
                clearCooldown(uuid);
                player.sendMessage(Utils.chat(ChatColor.RED + "You moved more than " + ChatColor.BOLD + MAX_MOVE_DISTANCE + ChatColor.RED + " blocks. " + getDisplayName() + ChatColor.RED + " timer ended."));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (getRemaining(uuid) > 0L) {
            Location from = startedLocations.get(uuid);
            checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (getRemaining(uuid) > 0L) {
            Location from = startedLocations.get(uuid);
            checkMovement(player, from, event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            clearCooldown(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (getRemaining(player) > 0L) {
                player.sendMessage(Utils.chat(ChatColor.RED + "You were damaged, " + getDisplayName() + ChatColor.RED + " timer ended."));
                clearCooldown(player);
            }
        }
    }

    private static final int NEAR_SEARCH_DISTANCE_BLOCKS = 24;

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null)
            return;

        Location nearest = LandMap.getNearestSafePosition(player, player.getLocation(), NEAR_SEARCH_DISTANCE_BLOCKS);
        if (nearest == null) {
            HCF.getPlugin().getCombatLogListener().safelyDisconnect(player, ChatColor.RED + "Unable to find a safe location, you have been safely logged out. ");
            return;
        }

        if (player.teleport(nearest, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            player.sendMessage(Utils.chat(ChatColor.YELLOW + getDisplayName() + ChatColor.YELLOW + " timer has teleported you to the nearest safe area."));
        }
    }

    public void run(Player player) {
        long remainingMillis = getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage(Utils.chat(getDisplayName() + ChatColor.BLUE + " timer is teleporting you in " + ChatColor.BOLD + DurationFormatter.getRemaining(remainingMillis, true, false) + ChatColor.BLUE
                    + '.'));
        }
    }
}
