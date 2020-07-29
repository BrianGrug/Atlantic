package me.scifi.hcf.eventgame.koth.argument;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.eventgame.faction.KothFaction;
import me.scifi.hcf.faction.type.Faction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.scifi.hcf.eventgame.CaptureZone;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used for setting the capture delay of an {@link KothFaction}.
 */
public class KothSetCapDelayArgument extends CommandArgument {

    private final HCF plugin;

    public KothSetCapDelayArgument(HCF plugin) {
        super("setcapdelay", "Sets the cap delay of a KOTH");
        this.plugin = plugin;
        this.aliases = new String[] { "setcapturedelay" };
        this.permission = "hcf.command.koth.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <kothName> <capDelay>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Faction faction = plugin.getManagerHandler().getFactionManager().getFaction(args[1]);

        if (faction == null || !(faction instanceof KothFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not a KOTH arena named '" + args[1] + "'.");
            return true;
        }

        long duration = JavaUtils.parse(StringUtils.join(args, ' ', 2, args.length));

        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }

        KothFaction kothFaction = (KothFaction) faction;
        CaptureZone captureZone = kothFaction.getCaptureZone();

        if (captureZone == null) {
            sender.sendMessage(ChatColor.RED + kothFaction.getDisplayName(sender) + ChatColor.RED + " does not have a capture zone.");
            return true;
        }

        // Update the remaining time if it is less.
        if (captureZone.isActive() && duration < captureZone.getRemainingCaptureMillis()) {
            captureZone.setRemainingCaptureMillis(duration);
        }

        captureZone.setDefaultCaptureMillis(duration);
        sender.sendMessage(ChatColor.YELLOW + "Set the capture delay of KOTH arena " + ChatColor.WHITE + kothFaction.getDisplayName(sender) + ChatColor.YELLOW + " to " + ChatColor.WHITE
                + DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.WHITE + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        return plugin.getManagerHandler().getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
