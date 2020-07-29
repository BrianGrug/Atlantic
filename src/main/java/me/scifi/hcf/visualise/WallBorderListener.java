package me.scifi.hcf.visualise;

import com.doctordark.util.cuboid.Cuboid;
import com.google.common.base.Predicate;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.type.ClaimableFaction;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.RoadFaction;
import me.scifi.hcf.timer.Timer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class WallBorderListener implements Listener {

    /**
     * The amount of newly generated blocks to inform what the purpose of the border is.
     */
    private static final int BORDER_PURPOSE_INFORM_THRESHOLD = 66;

    private static final int WALL_BORDER_HEIGHT_BELOW_DIFF = 3;
    private static final int WALL_BORDER_HEIGHT_ABOVE_DIFF = 4;
    private static final int WALL_BORDER_HORIZONTAL_DISTANCE = 7;

    private final boolean useTaskInstead; // TODO: configurable
    private final Map<UUID, BukkitTask> wallBorderTask = new HashMap<>();
    private final HCF plugin;

    public WallBorderListener(HCF plugin) {
        this.plugin = plugin;
        this.useTaskInstead = plugin.getRandom().nextBoolean() ? false : false; // TODO: configurable, temporary to suppress IDE warnings about always true;
    }

    private static final class WarpTimerRunnable extends BukkitRunnable {

        private WallBorderListener listener;
        private Player player;

        private double lastX = Double.MAX_VALUE;
        private double lastY = Double.MAX_VALUE;
        private double lastZ = Double.MAX_VALUE;

        public WarpTimerRunnable(WallBorderListener listener, Player player) {
            this.listener = listener;
            this.player = player;
        }

        @Override
        public void run() {
            Location location = player.getLocation();

            // Check if the player moved or is AFK.
            double x = location.getBlockX();
            double y = location.getBlockY();
            double z = location.getBlockZ();
            if (this.lastX == x && this.lastY == y && this.lastZ == z) {
                return;
            }

            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.listener.handlePositionChanged(player, player.getWorld(), (int) x, (int) y, (int) z);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.listener = null;
            this.player = null;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!this.useTaskInstead)
            return;
        BukkitTask task = wallBorderTask.remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.useTaskInstead) {
            wallBorderTask.put(player.getUniqueId(), new WarpTimerRunnable(this, player).runTaskTimerAsynchronously(plugin, 3L, 3L));
            return;
        }

        // For some reason, sending the packet on the initial join doesn't display the visual
        // blocks due to an error on Mojangs end, well at least with 1.7.10 so we have to send
        // it a little later.
        Location now = player.getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                Location location = player.getLocation();
                if (now.equals(location)) {
                    WallBorderListener.this.handlePositionChanged(player, location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                }
            }
        }.runTaskLater(plugin, 4L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.useTaskInstead)
            return;
        Location to = event.getTo();
        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();

        Location from = event.getFrom();
        if (from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ) {
            this.handlePositionChanged(event.getPlayer(), to.getWorld(), toX, toY, toZ);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.onPlayerMove(event); // PlayerTeleportEvent doesn't have a chained handlerList
    }

    private void handlePositionChanged(Player player, World toWorld, int toX, int toY, int toZ) {
        final VisualType visualType;
        final Timer relevantTimer;
        if (plugin.getManagerHandler().getTimerManager().getCombatTimer().getRemaining(player) > 0L) {
            visualType = VisualType.SPAWN_BORDER;
            relevantTimer = plugin.getManagerHandler().getTimerManager().getCombatTimer();
        } else if (plugin.getManagerHandler().getTimerManager().getPvpTimer().getRemaining(player) > 0L) {
            visualType = VisualType.CLAIM_BORDER;
            relevantTimer = plugin.getManagerHandler().getTimerManager().getPvpTimer();
        } else
            return;

        // Clear any visualises that are no longer within distance.
        plugin.getManagerHandler().getVisualiseHandler().clearVisualBlocks(player, visualType, new Predicate<VisualBlock>() {
            @Override
            public boolean apply(VisualBlock visualBlock) {
                Location other = visualBlock.getLocation();
                return other.getWorld().equals(toWorld)
                        && (Math.abs(toX - other.getBlockX()) > WALL_BORDER_HORIZONTAL_DISTANCE || Math.abs(toY - other.getBlockY()) > WALL_BORDER_HEIGHT_ABOVE_DIFF || Math.abs(toZ
                                - other.getBlockZ()) > WALL_BORDER_HORIZONTAL_DISTANCE);
            }
        });

        // Values used to calculate the new visual cuboid height.
        int minHeight = toY - WALL_BORDER_HEIGHT_BELOW_DIFF;
        int maxHeight = toY + WALL_BORDER_HEIGHT_ABOVE_DIFF;
        int minX = toX - WALL_BORDER_HORIZONTAL_DISTANCE;
        int maxX = toX + WALL_BORDER_HORIZONTAL_DISTANCE;
        int minZ = toZ - WALL_BORDER_HORIZONTAL_DISTANCE;
        int maxZ = toZ + WALL_BORDER_HORIZONTAL_DISTANCE;
        Collection<Claim> added = new HashSet<>();
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                Faction faction = plugin.getManagerHandler().getFactionManager().getFactionAt(toWorld, x, z);
                if (!(faction instanceof ClaimableFaction))
                    continue;

                // Special case for these.
                if (visualType == VisualType.SPAWN_BORDER) {
                    if (!faction.isSafezone()) {
                        continue;
                    }
                } else if (visualType == VisualType.CLAIM_BORDER) {
                    if (faction instanceof RoadFaction || faction.isSafezone()) {
                        continue;
                    }
                }

                Collection<Claim> claims = ((ClaimableFaction) faction).getClaims();
                for (Claim claim : claims) {
                    if (toWorld.equals(claim.getWorld())) {
                        added.add(claim);
                    }
                }
            }
        }

        if (!added.isEmpty()) {
            int generated = 0;
            Iterator<Claim> iterator = added.iterator();
            while (iterator.hasNext()) {
                Claim claim = iterator.next();
                Collection<Vector> edges = claim.edges(); // TODO: Don't use #edges(), find a way just to get for surrounding x and z loop
                for (Vector edge : edges) {
                    // Only show those in range.
                    if (Math.abs(edge.getBlockX() - toX) > WALL_BORDER_HORIZONTAL_DISTANCE)
                        continue;
                    if (Math.abs(edge.getBlockZ() - toZ) > WALL_BORDER_HORIZONTAL_DISTANCE)
                        continue;

                    Location location = edge.toLocation(toWorld);
                    if (location != null) {
                        Location first = location.clone();
                        first.setY(minHeight);

                        Location second = location.clone();
                        second.setY(maxHeight);

                        if (useTaskInstead) {
                            generated += plugin.getManagerHandler().getVisualiseHandler().generateAsync(player, new Cuboid(first, second), visualType, false).size();
                        } else {
                            generated += plugin.getManagerHandler().getVisualiseHandler().generate(player, new Cuboid(first, second), visualType, false).size();
                        }
                    }
                }

                if (generated >= BORDER_PURPOSE_INFORM_THRESHOLD) {
                    player.sendMessage(Utils.chat(ChatColor.RED + "That wall prevents you from entering " + claim.getFaction().getDisplayName(player) + ChatColor.RED + " whilst you have your "
                            + relevantTimer.getDisplayName() + ChatColor.RED + " timer."));
                }

                iterator.remove();
            }
        }
    }
}
