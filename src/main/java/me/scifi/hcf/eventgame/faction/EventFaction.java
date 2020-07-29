package me.scifi.hcf.eventgame.faction;

import com.doctordark.util.cuboid.Cuboid;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.claim.ClaimHandler;
import me.scifi.hcf.faction.type.ClaimableFaction;
import me.scifi.hcf.faction.type.Faction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import me.scifi.hcf.eventgame.CaptureZone;
import me.scifi.hcf.eventgame.EventType;

import java.util.List;
import java.util.Map;

public abstract class EventFaction extends ClaimableFaction {

    public EventFaction(String name) {
        super(name);
        this.setDeathban(true);
    }

    public EventFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(Faction faction) {
        if(getEventType() == EventType.KOTH) {
            return Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.EVENT.KOTH.DISPLAYNAME").replace("%name%", name));
        }
        return Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.EVENT.CONQUEST.DISPLAYNAME").replace("%name%",name));
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        if (getEventType() == EventType.KOTH) {
            return Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.EVENT.KOTH.DISPLAYNAME").replace("%name%", name));
        }
        return Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.EVENT.CONQUEST.DISPLAYNAME").replace("%name%",name));
    }

    public String getScoreboardName() {
      if(getEventType() == EventType.KOTH){
        return Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.EVENT.KOTH.FORMATTED").replace("%name%", name));
    }
        return Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.EVENT.CONQUEST.FORMATTED").replace("%name%",name));
    }

    /**
     * Sets the {@link Cuboid} area of this {@link KothFaction}.
     *
     * @param cuboid
     *            the {@link Cuboid} to set
     * @param sender
     *            the {@link CommandSender} setting the claim
     */
    public void setClaim(Cuboid cuboid, CommandSender sender) {
        removeClaims(getClaims(), sender);

        // Now add the new claim.
        Location min = cuboid.getMinimumPoint();
        min.setY(ClaimHandler.MIN_CLAIM_HEIGHT);

        Location max = cuboid.getMaximumPoint();
        max.setY(ClaimHandler.MAX_CLAIM_HEIGHT);

        addClaim(new Claim(this, min, max), sender);
    }

    /**
     * Gets the {@link EventType} of this {@link CapturableFaction}.
     *
     * @return the {@link EventType}
     */
    public abstract EventType getEventType();

    /**
     * Gets the {@link CaptureZone}s for this {@link CapturableFaction}.
     *
     * @return list of {@link CaptureZone}s
     */
    public abstract List<CaptureZone> getCaptureZones();
}
