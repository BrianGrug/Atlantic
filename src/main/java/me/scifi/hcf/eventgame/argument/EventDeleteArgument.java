package me.scifi.hcf.eventgame.argument;

import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.scifi.hcf.eventgame.faction.EventFaction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used for deleting an {@link EventFaction}.
 */
public class EventDeleteArgument extends CommandArgument {

    private final HCF plugin;

    public EventDeleteArgument(HCF plugin) {
        super("delete", "Deletes an event");
        this.plugin = plugin;
        this.aliases = new String[] { "remove", "del" };
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <eventName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Faction faction = plugin.getManagerHandler().getFactionManager().getFaction(args[1]);

        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }

        if (plugin.getManagerHandler().getFactionManager().removeFaction(faction, sender)) {
            sender.sendMessage(ChatColor.YELLOW + "Deleted event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        return plugin.getManagerHandler().getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
