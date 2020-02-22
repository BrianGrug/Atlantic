package com.doctordark.util;

import org.bukkit.plugin.java.*;
import com.google.common.collect.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import org.bukkit.block.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import javax.annotation.*;
import java.util.*;

public class SignHandler implements Listener {
    private final Multimap<UUID, SignChange> signUpdateMap;
    private final JavaPlugin plugin;

    public SignHandler(final JavaPlugin plugin) {
        this.signUpdateMap = HashMultimap.create();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerQuitEvent event) {
        this.cancelTasks(event.getPlayer(), null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.cancelTasks(event.getPlayer(), null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        this.cancelTasks(event.getPlayer(), null, false);
    }

    public boolean showLines(final Player player, final Sign sign, final String[] newLines, final long ticks, final boolean forceChange) {
        final String[] lines = sign.getLines();
        if (Arrays.equals(lines, newLines)) {
            return false;
        }
        final Collection<SignChange> signChanges = this.getSignChanges(player);
        final Iterator<SignChange> iterator = signChanges.iterator();
        while (iterator.hasNext()) {
            final SignChange signChange = iterator.next();
            if (signChange.sign.equals(sign)) {
                if (!forceChange && Arrays.equals(signChange.newLines, newLines)) {
                    return false;
                }
                signChange.runnable.cancel();
                iterator.remove();
                break;
            }
        }
        final Location location = sign.getLocation();
        player.sendSignChange(location, newLines);
        SignChange signChange;
        if (signChanges.add(signChange = new SignChange(sign, newLines))) {
            final Block block = sign.getBlock();
            final BlockState previous = block.getState();
            final BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    if (SignHandler.this.signUpdateMap.remove((Object) player.getUniqueId(), (Object) signChange) && previous.equals(block.getState())) {
                        player.sendSignChange(location, lines);
                    }
                }
            };
            runnable.runTaskLater((Plugin) this.plugin, ticks);
            signChange.runnable = runnable;
        }
        return true;
    }

    public Collection<SignChange> getSignChanges(final Player player) {
        return (Collection<SignChange>) this.signUpdateMap.get(player.getUniqueId());
    }

    public void cancelTasks(@Nullable final Sign sign) {
        final Iterator<SignChange> iterator = this.signUpdateMap.values().iterator();
        while (iterator.hasNext()) {
            final SignChange signChange = iterator.next();
            if (sign == null || signChange.sign.equals(sign)) {
                signChange.runnable.cancel();
                signChange.sign.update();
                iterator.remove();
            }
        }
    }

    public void cancelTasks(final Player player, @Nullable final Sign sign, final boolean revertLines) {
        final UUID uuid = player.getUniqueId();
        final Iterator<Map.Entry<UUID, SignChange>> iterator = this.signUpdateMap.entries().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, SignChange> entry = iterator.next();
            if (entry.getKey().equals(uuid)) {
                final SignChange signChange = entry.getValue();
                if (sign != null && !signChange.sign.equals(sign)) {
                    continue;
                }
                if (revertLines) {
                    player.sendSignChange(signChange.sign.getLocation(), signChange.sign.getLines());
                }
                signChange.runnable.cancel();
                iterator.remove();
            }
        }
    }

    private static final class SignChange {
        public BukkitRunnable runnable;
        public final Sign sign;
        public final String[] newLines;

        public SignChange(final Sign sign, final String[] newLines) {
            this.sign = sign;
            this.newLines = newLines;
        }
    }
}
