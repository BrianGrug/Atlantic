package me.scifi.hcf.faction;

import com.doctordark.util.Config;
import com.doctordark.util.JavaUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.event.cause.ClaimChangeCause;
import me.scifi.hcf.faction.struct.ChatChannel;
import me.scifi.hcf.faction.struct.Role;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.craftbukkit.v1_7_R4.util.LongHash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.event.FactionClaimChangedEvent;
import me.scifi.hcf.faction.event.FactionCreateEvent;
import me.scifi.hcf.faction.event.FactionRemoveEvent;
import me.scifi.hcf.faction.event.FactionRenameEvent;
import me.scifi.hcf.faction.event.PlayerJoinedFactionEvent;
import me.scifi.hcf.faction.event.PlayerLeftFactionEvent;
import me.scifi.hcf.faction.type.ClaimableFaction;
import me.scifi.hcf.faction.type.EndPortalFaction;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.faction.type.RoadFaction;
import me.scifi.hcf.faction.type.SpawnFaction;
import me.scifi.hcf.faction.type.WarzoneFaction;
import me.scifi.hcf.faction.type.WildernessFaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FlatFileFactionManager implements Listener, FactionManager {

    // The default claimless factions.
    private final WarzoneFaction warzone;
    private final WildernessFaction wilderness;

    // Cached for faster lookup for factions. Potentially usage Guava Cache for
    // future implementations (database).
    private final Table<String, Long, Claim> claimPositionMap = HashBasedTable.create();
    private final ConcurrentMap<UUID, UUID> factionPlayerUuidMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> factionNameMap = new CaseInsensitiveMap<>();

    private Config config;
    private final HCF plugin;

    public FlatFileFactionManager(HCF plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        this.warzone = new WarzoneFaction();
        this.wilderness = new WildernessFaction();
        this.reloadFactionData();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
        this.factionPlayerUuidMap.put(event.getPlayerUUID(), event.getFaction().getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        this.factionPlayerUuidMap.remove(event.getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        this.factionNameMap.remove(event.getOriginalName());
        this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
    }

    // Cache the claimed land locations
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionClaim(FactionClaimChangedEvent event) {
        for (Claim claim : event.getAffectedClaims()) {
            this.cacheClaim(claim, event.getCause());
        }
    }

    @Override
    public Collection<ClaimableFaction> getClaimableFactions() {
        return null;
    }

    @Override
    @Deprecated
    public Map<String, UUID> getFactionNameMap() {
        return this.factionNameMap;
    }

    @Override
    public ImmutableList<Faction> getFactions() {
        return ImmutableList.copyOf(this.factionUUIDMap.values());
    }

    @Override
    public Claim getClaimAt(World world, int x, int z) {
        return this.claimPositionMap.get(world.getName(), LongHash.toLong(x, z));
    }

    @Override
    public Claim getClaimAt(Location location) {
        return this.getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    @Override
    public Faction getFactionAt(World world, int x, int z) {
        World.Environment environment = world.getEnvironment();

        Claim claim = this.getClaimAt(world, x, z);
        if (claim != null) {
            Faction faction = claim.getFaction();
            if (faction != null) {
                return faction;
            }
        }

        if (environment == World.Environment.THE_END) { // the End doesn't have a Warzone.
            return this.warzone;
        }

        // Nether Warzone should be 8 times smaller allowing for Gold Farms to actually be efficient.
        int warzoneRadius = ConfigurationService.WARZONE_RADIUS;
        if (environment == World.Environment.NETHER) {
            warzoneRadius /= 8;
        }

        return Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius ? this.wilderness : this.warzone;
    }

    @Override
    public Faction getFactionAt(Location location) {
        return getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    @Override
    public Faction getFactionAt(Block block) {
        return getFactionAt(block.getLocation());
    }

    @Override
    public Faction getFaction(String factionName) {
        UUID uuid = factionNameMap.get(factionName);
        return uuid == null ? null : factionUUIDMap.get(uuid);
    }

    @Override
    public Faction getFaction(UUID factionUUID) {
        return factionUUIDMap.get(factionUUID);
    }

    @Override
    public PlayerFaction getPlayerFaction(UUID playerUUID) {
        UUID uuid = factionPlayerUuidMap.get(playerUUID);
        Faction faction = uuid == null ? null : factionUUIDMap.get(uuid);
        return faction instanceof PlayerFaction ? (PlayerFaction) faction : null;
    }

    @Override
    public PlayerFaction getPlayerFaction(Player player) {
        return getPlayerFaction(player.getUniqueId());
    }

    @Override
    public PlayerFaction getContainingPlayerFaction(String search) {
        OfflinePlayer target = JavaUtils.isUUID(search) ? Bukkit.getOfflinePlayer(UUID.fromString(search)) : Bukkit.getOfflinePlayer(search); // TODO: breaking
        return target.hasPlayedBefore() || target.isOnline() ? getPlayerFaction(target.getUniqueId()) : null;
    }

    @Override
    public Faction getContainingFaction(String search) {
        Faction faction = getFaction(search);
        if (faction != null)
            return faction;

        UUID playerUUID = Bukkit.getOfflinePlayer(search).getUniqueId(); // TODO: breaking
        if (playerUUID != null)
            return getPlayerFaction(playerUUID);

        return null;
    }

    @Override
    public boolean containsFaction(Faction faction) {
        return factionNameMap.containsKey(faction.getName());
    }

    @Override
    public boolean createFaction(Faction faction) {
        return createFaction(faction, Bukkit.getConsoleSender());
    }

    @Override
    public boolean createFaction(Faction faction, CommandSender sender) {
        // Automatically attempt to make the sender as the leader.
        if (faction instanceof PlayerFaction && sender instanceof Player) {
            Player player = (Player) sender;
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if (!playerFaction.addMember(sender, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) {
                return false;
            }
        }

        if (this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
            return false; // faction already exists.
        }

        this.factionNameMap.put(faction.getName(), faction.getUniqueID());

        FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
        Bukkit.getPluginManager().callEvent(createEvent);
        return !createEvent.isCancelled();
    }

    @Override
    public boolean removeFaction(Faction faction, CommandSender sender) {
        if (!this.factionUUIDMap.containsKey(faction.getUniqueID())) {
            return false;
        }

        FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
        Bukkit.getPluginManager().callEvent(removeEvent);
        if (removeEvent.isCancelled()) {
            return false;
        }

        this.factionUUIDMap.remove(faction.getUniqueID());
        this.factionNameMap.remove(faction.getName());

        // Let the plugin know the claims should be lost.
        if (faction instanceof ClaimableFaction) {
            Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, ((ClaimableFaction) faction).getClaims()));
        }

        // Let the plugin know these players should have left.
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            for (PlayerFaction ally : playerFaction.getAlliedFactions()) {
                ally.getRelations().remove(faction.getUniqueID());
            }

            for (UUID uuid : playerFaction.getMembers().keySet()) {
                playerFaction.removeMember(sender, null, uuid, true, true);
            }
        }

        return true;
    }

    private void cacheClaim(Claim claim, ClaimChangeCause cause) {
        Preconditions.checkNotNull(claim, "Claim cannot be null");
        Preconditions.checkNotNull(cause, "Cause cannot be null");
        Preconditions.checkArgument(cause != ClaimChangeCause.RESIZE, "Cannot cache claims of resize type");

        World world = claim.getWorld();
        if (world == null) {
            return; // safe-guard if Nether or End is disabled for example
        }

        int minX = Math.min(claim.getX1(), claim.getX2());
        int maxX = Math.max(claim.getX1(), claim.getX2());
        int minZ = Math.min(claim.getZ1(), claim.getZ2());
        int maxZ = Math.max(claim.getZ1(), claim.getZ2());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (cause == ClaimChangeCause.CLAIM) {
                    this.claimPositionMap.put(world.getName(), LongHash.toLong(x, z), claim);
                } else if (cause == ClaimChangeCause.UNCLAIM) {
                    this.claimPositionMap.remove(world.getName(), LongHash.toLong(x, z));
                }
            }
        }
    }

    private void cacheFaction(Faction faction) {
        factionNameMap.put(faction.getName(), faction.getUniqueID());
        factionUUIDMap.put(faction.getUniqueID(), faction);

        // Put the claims in the cache.
        if (faction instanceof ClaimableFaction) {
            for (Claim claim : ((ClaimableFaction) faction).getClaims()) {
                this.cacheClaim(claim, ClaimChangeCause.CLAIM);
            }
        }

        // Put the members in the cache too.
        if (faction instanceof PlayerFaction) {
            for (FactionMember factionMember : ((PlayerFaction) faction).getMembers().values()) {
                this.factionPlayerUuidMap.put(factionMember.getUniqueId(), faction.getUniqueID());
            }
        }
    }

    @Override
    public void reloadFactionData() {
        this.factionNameMap.clear();
        this.config = new Config(plugin, "factions", plugin.getDataFolder().getAbsolutePath());

        Object object = config.get("factions");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String factionName : section.getKeys(false)) {
                Object next = config.get(section.getCurrentPath() + '.' + factionName);
                if (next instanceof Faction) {
                    cacheFaction((Faction) next);
                }
            }
        } else if (object instanceof List<?>) {
            List<?> list = (List<?>) object;
            for (Object next : list) {
                if (next instanceof Faction) {
                    cacheFaction((Faction) next);
                }
            }
        }

        Set<Faction> adding = new HashSet<>();
        if (!factionNameMap.containsKey("NorthRoad")) { // TODO: more reliable
            adding.add(new RoadFaction.NorthRoadFaction());
            adding.add(new RoadFaction.EastRoadFaction());
            adding.add(new RoadFaction.SouthRoadFaction());
            adding.add(new RoadFaction.WestRoadFaction());
        }

        if (!factionNameMap.containsKey("Spawn")) { // TODO: more reliable
            adding.add(new SpawnFaction());
        }

        if (!this.factionNameMap.containsKey("EndPortal")) { // TODO: more reliable
            adding.add(new EndPortalFaction());
        }

        // Now load the Spawn, etc factions.
        for (Faction added : adding) {
            this.cacheFaction(added);
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Faction " + added.getName() + " not found, created.");
        }
    }

    @Override
    public void saveFactionData() {
        this.config.set("factions", new ArrayList<>(factionUUIDMap.values()));
        this.config.save();
    }
}