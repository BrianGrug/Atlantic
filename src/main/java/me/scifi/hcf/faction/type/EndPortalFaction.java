package me.scifi.hcf.faction.type;

import me.scifi.hcf.faction.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import me.scifi.hcf.ConfigurationService;

import java.util.Map;

/**
 * Represents the {@link EndPortalFaction}.
 */
public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable {

    public EndPortalFaction() {
        super("EndPortal");

        World overworld = Bukkit.getWorld("world");
        int maxHeight = overworld.getMaxHeight();
        int min = ConfigurationService.END_PORTAL_CENTER - ConfigurationService.END_PORTAL_RADIUS;
        int max = ConfigurationService.END_PORTAL_CENTER + ConfigurationService.END_PORTAL_RADIUS;

        this.safezone = false;
    }

    public EndPortalFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public boolean isDeathban() {
        return false;
    }
}
