package me.scifi.hcf.faction;

import com.doctordark.internal.com.doctordark.base.BasePlugin;
import com.doctordark.util.BukkitUtils;
import com.google.common.base.Objects;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.claim.ClaimHandler;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.scifi.hcf.HCF;
import me.scifi.hcf.visualise.VisualBlockData;
import me.scifi.hcf.visualise.VisualType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LandMap {
    private static final int FACTION_MAP_RADIUS_BLOCKS;

    static {
        FACTION_MAP_RADIUS_BLOCKS = 22;
    }

    @SuppressWarnings("deprecation")
    public static boolean updateMap(final Player player, final HCF plugin, final VisualType visualType, final boolean inform) {
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final int locationX = location.getBlockX();
        final int locationZ = location.getBlockZ();
        final int minimumX = locationX - FACTION_MAP_RADIUS_BLOCKS;
        final int minimumZ = locationZ - FACTION_MAP_RADIUS_BLOCKS;
        final int maximumX = locationX + FACTION_MAP_RADIUS_BLOCKS;
        final int maximumZ = locationZ + FACTION_MAP_RADIUS_BLOCKS;
        final Set<Claim> board = new LinkedHashSet<Claim>();
        if(visualType != VisualType.CLAIM_MAP) {
            player.sendMessage(ChatColor.RED + "Not supported: " + visualType.name().toLowerCase() + '.');
            return false;
        }

        for(int x = minimumX; x <= maximumX; ++x) {
            for(int z = minimumZ; z <= maximumZ; ++z) {
                final Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
                if(claim != null) {
                    board.add(claim);
                }
            }
        }
        if(board.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No claims are in your visual range to display.");
            return false;
        }
        LOOP_MAIN:   for(final Claim claim2 : board) {
            final int maxHeight = Math.min(world.getMaxHeight(), 256);
            final Location[] corners = claim2.getCornerLocations();
            final List<Location> shown = new ArrayList<Location>(maxHeight * corners.length);
            for(final Location corner : corners) {
                for(int y = 0; y < maxHeight; ++y) {
                    shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
                }
            }
            final Map<Location, VisualBlockData> dataMap = plugin.getVisualiseHandler().generate(player, shown, visualType, true);
            if(dataMap.isEmpty()) {
                continue LOOP_MAIN;
            }
            String materialName = ChatColor.RED + "Error!";
            LOOP_1:
            for(VisualBlockData visualBlockData : dataMap.values()){
                if(visualBlockData.getItemType() == Material.STAINED_GLASS){
                    continue LOOP_1;
                }else{
                    materialName = BasePlugin.getPlugin().getItemDb().getName(new ItemStack(visualBlockData.getItemType()));
                    break LOOP_1;
                }
            }
            if(!inform) {
                continue LOOP_MAIN;
            }
            player.sendMessage(claim2.getFaction().getDisplayName(player) +ChatColor.YELLOW  +" owns claim displayed by the " + ChatColor.AQUA + materialName);
        }
        return true;
    }

    public static Location getNearestSafePosition(final Player player, final Location origin, final int searchRadius) {
        final Location location = player.getLocation();
        player.getWorld();
        location.getBlockX();
        location.getBlockZ();
        Claim  claimAt = HCF.getPlugin().getFactionManager().getClaimAt(player.getLocation());
        Location closest = null;
        for(Location claim : claimAt.getCornerLocations()) {
            if(closest == null){
                closest = claim;
            }else {
                if(claim.distance(player.getLocation()) < closest.distance(player.getLocation())){
                    closest = claim;
                }
            }
        }
        if(closest == null){
            return null;
        }
        closest.add(0, 1, 0);
        return BukkitUtils.getHighestLocation(closest);
    }
}