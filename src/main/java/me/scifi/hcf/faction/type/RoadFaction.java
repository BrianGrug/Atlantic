package me.scifi.hcf.faction.type;

import com.doctordark.util.BukkitUtils;
import me.scifi.hcf.faction.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import me.scifi.hcf.ConfigurationService;

import java.util.Map;

/**
 * Represents the {@link RoadFaction}.
 * <p>
 * TODO: Needs cleanup
 */
public class RoadFaction extends ClaimableFaction implements ConfigurationSerializable {
    public RoadFaction(final String name) {
        super(name);
    }

    public RoadFaction(final Map<String, Object> map) {
        super(map);
    }

    public String getDisplayName(final CommandSender sender) {
        return ChatColor.GOLD + this.getName().replace("st", "st ").replace("th", "th ");
    }

    @Override
    public void printDetails(final CommandSender sender) {

        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(this.getDisplayName(sender));
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.GRAY + "None");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }

    public static class NorthRoadFaction extends RoadFaction implements ConfigurationSerializable {
        public NorthRoadFaction() {
            super("NorthRoad");
        }

        public NorthRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }

    public static class EastRoadFaction extends RoadFaction implements ConfigurationSerializable {
        public EastRoadFaction() {
            super("EastRoad");
        }

        public EastRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }

    public static class SouthRoadFaction extends RoadFaction implements ConfigurationSerializable {
        public SouthRoadFaction() {
            super("SouthRoad");

        }

        public SouthRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }

    public static class WestRoadFaction extends RoadFaction implements ConfigurationSerializable {
        public WestRoadFaction() {
            super("WestRoad");
        }

        public WestRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }
}
