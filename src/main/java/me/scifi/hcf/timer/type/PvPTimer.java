package me.scifi.hcf.timer.type;

import com.doctordark.util.BukkitUtils;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.event.FactionClaimChangedEvent;
import me.scifi.hcf.faction.event.PlayerClaimEnterEvent;
import me.scifi.hcf.faction.event.cause.ClaimChangeCause;
import me.scifi.hcf.faction.type.ClaimableFaction;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.faction.type.RoadFaction;
import me.scifi.hcf.timer.event.TimerClearEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.timer.PlayerTimer;
import me.scifi.hcf.timer.TimerCooldown;
import me.scifi.hcf.visualise.VisualType;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Timer used to apply PVP Protection to {@link Player}s.
 */
public class PvPTimer extends PlayerTimer implements Listener {

    // TODO: Future proof
    private static final String PVP_COMMAND = "/pvp enable";

    // The PlayerPickupItemEvent spams if cancelled, needs a delay between messages to look clean.
    private static final long ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
    private static final long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
    private static final String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";

    private final Map<UUID, Long> itemUUIDPickupDelays = new HashMap<>();
    private final HCF plugin;

    public PvPTimer(HCF plugin) {
        super(plugin.messagesYML.getString("SCOREBOARD.PVPTIMER.NAME"), TimeUnit.SECONDS.toMillis(HCF.getPlugin().messagesYML.getLong("SCOREBOARD.PVPTIMER.LENGTH")));
        this.plugin = plugin;
    }

    @Override
    public String getScoreboardPrefix() {
        return plugin.messagesYML.getString("SCOREBOARD.PVPTIMER.PREFIX");
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player != null) {
            plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
            player.sendMessage(Utils.chat(ChatColor.RED.toString() + ChatColor.BOLD + "You no longer have " + getDisplayName() + ChatColor.RED + ChatColor.BOLD + ", good luck!"));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(TimerClearEvent event) {
        if (event.getTimer() == this) {
            Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire(optionalUserUUID.get());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClaimChange(FactionClaimChangedEvent event) {
        if (event.getCause() != ClaimChangeCause.CLAIM) {
            return;
        }

        Collection<Claim> claims = event.getAffectedClaims();
        for (Claim claim : claims) {
            Collection<Player> players = claim.getPlayers();
            if (players.isEmpty()) {
                continue;
            }

            Location location = new Location(claim.getWorld(), claim.getMinimumX() - 1, 0, claim.getMinimumZ() - 1);
            location = BukkitUtils.getHighestLocation(location, location);
            for (Player player : players) {
                if (getRemaining(player) > 0L && player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                    player.sendMessage(Utils.chat(ChatColor.RED + "Faction land was claimed where you were standing. As you still have your " + getName() + " timer, you were teleported away."));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
      if(!HCF.getPlugin().getConfig().getBoolean("kit-map")) {
          if (this.setCooldown(player, player.getUniqueId(), defaultCooldown, true)) {
              this.setPaused(player.getUniqueId(), false);
              player.sendMessage(Utils.chat(ChatColor.GREEN + "Your " + getDisplayName() + ChatColor.GREEN + " timer has been activated."));
          }
       }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();
        Location location = player.getLocation();
        Collection<ItemStack> drops = event.getDrops();
        if (!drops.isEmpty()) {
            Iterator<ItemStack> iterator = drops.iterator();

            // Drop the items manually so we can add meta to prevent
            // PVP Protected players from collecting them.
            long stamp = System.currentTimeMillis() + +ITEM_PICKUP_DELAY;
            while (iterator.hasNext()) {
                itemUUIDPickupDelays.put(world.dropItemNaturally(location, iterator.next()).getUniqueId(), stamp);
                iterator.remove();
            }
        }

        clearCooldown(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        long remaining = getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(Utils.chat(ChatColor.RED + "You cannot empty buckets as your " + getDisplayName() + ChatColor.RED + " timer is active."));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null)
            return;
        long remaining = getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(Utils.chat(ChatColor.RED + "You cannot ignite items as your " + getDisplayName() + ChatColor.RED + " timer is active."));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        long remaining = getRemaining(player);
        if (remaining > 0L) {
            UUID itemUUID = event.getItem().getUniqueId();
            Long delay = itemUUIDPickupDelays.get(itemUUID);
            if (delay == null)
                return;

            // The item has been spawned for over the required pickup time for
            // PVP Protected players, let them pick it up.
            long millis = System.currentTimeMillis();
            if ((delay - millis) > 0L) {
                event.setCancelled(true);

                // Don't let the pickup event spam the player.
                List<MetadataValue> value = player.getMetadata(ITEM_PICKUP_MESSAGE_META_KEY);
                if (value != null && !value.isEmpty() && value.get(0).asLong() - millis <= 0L) {
                    player.setMetadata(ITEM_PICKUP_MESSAGE_META_KEY, new FixedMetadataValue(plugin, millis + ITEM_PICKUP_MESSAGE_DELAY));
                    player.sendMessage(Utils.chat(ChatColor.RED + "You cannot pick this item up for another " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED
                            + " as your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED
                            + " remaining]"));
                }
            } else
                itemUUIDPickupDelays.remove(itemUUID);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TimerCooldown runnable = cooldowns.get(player.getUniqueId());
        if (runnable != null && runnable.getRemaining() > 0L) {
            runnable.setPaused(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            if(!plugin.getConfig().getBoolean("kit-map")){
            if (this.canApply() && this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, true)) {
                this.setPaused(player.getUniqueId(), true);
                player.sendMessage(Utils.chat(ChatColor.GREEN + "You now have your " + getDisplayName() + ChatColor.GREEN + " timer."));
            }
        } else {
                // If a player has their timer paused and they are not in a safezone, un-pause for them.
                // We do this because disconnection pauses PVP Protection.
                if (this.isPaused(player) && getRemaining(player) > 0L && !plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
                    this.setPaused(player.getUniqueId(), false);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClaimEnterMonitor(PlayerClaimEnterEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            clearCooldown(player);
            return;
        }

        boolean flag = getRemaining(player.getUniqueId()) > 0L;
        if (flag) {
            Faction toFaction = event.getToFaction();
            Faction fromFaction = event.getFromFaction();

            if (fromFaction.isSafezone() && !toFaction.isSafezone()) {
                player.sendMessage(Utils.chat(ChatColor.RED + "Your " + getDisplayName() + ChatColor.RED + " timer is no longer paused."));
                this.setPaused(player.getUniqueId(), false);
            } else if (!fromFaction.isSafezone() && toFaction.isSafezone()) {
                player.sendMessage(Utils.chat(ChatColor.GREEN + "Your " + getDisplayName() + ChatColor.GREEN + " timer is now paused."));
                this.setPaused(player.getUniqueId(), true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Player player = event.getPlayer();
        Faction toFaction = event.getToFaction();
        long remaining; // lazy load
        if (toFaction instanceof ClaimableFaction && (remaining = getRemaining(player)) > 0L) {
            if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
                // Allow player to enter own claim, but just remove PVP Protection when teleporting.
                PlayerFaction playerFaction; // lazy-load

                if (toFaction instanceof PlayerFaction && (playerFaction = plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction == toFaction) {
                    player.sendMessage(Utils.chat(ChatColor.RED + "You have entered your own claim, therefore your " + getDisplayName() + ChatColor.RED + " timer was cleared."));
                    clearCooldown(player);
                    return;
                }
            }

            if (!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
                event.setCancelled(true);
                player.sendMessage(Utils.chat(ChatColor.RED + "You cannot enter " + toFaction.getDisplayName(player) + ChatColor.RED + " whilst your " + getDisplayName() + ChatColor.RED + " timer is active ["
                        + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. " + "Use '" + ChatColor.GOLD + PVP_COMMAND + ChatColor.RED
                        + "' to remove this timer."));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attacker == null)
                return;

            long remaining;
            Player player = (Player) entity;
            if ((remaining = getRemaining(player)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(Utils.chat(ChatColor.RED + player.getName() + " has their " + getDisplayName() + ChatColor.RED + " timer for another " + ChatColor.BOLD
                        + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + '.'));

                return;
            }

            if ((remaining = getRemaining(attacker)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(Utils.chat( ChatColor.RED + "You cannot attack players whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD
                        + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. Use '" + ChatColor.GOLD + PVP_COMMAND + ChatColor.RED + "' to allow pvp."));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
            for (LivingEntity livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player) {
                    if (getRemaining((Player) livingEntity) > 0L) {
                        event.setIntensity(livingEntity, 0);
                    }
                }
            }
        }
    }

    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite, @Nullable Predicate<Long> callback) {
        return this.canApply() && super.setCooldown(player, playerUUID, duration, overwrite, callback);
    }

    private boolean canApply() {
        return !plugin.getEotwHandler().isEndOfTheWorld() && !ConfigurationService.KIT_MAP && plugin.getSotwTimer().getSotwRunnable() == null;
    }
}
