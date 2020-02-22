package me.scifi.hcf.eventgame.argument;

import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.Faction;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.scifi.hcf.eventgame.EventType;
import me.scifi.hcf.eventgame.faction.ConquestFaction;
import me.scifi.hcf.eventgame.faction.KothFaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventCreateArgument extends CommandArgument {

    private final HCF plugin;

    public EventCreateArgument(HCF plugin) {
        super("create", "Defines a new event", new String[] { "make", "define" });
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <eventName> <Conquest|KOTH>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Faction faction = plugin.getFactionManager().getFaction(args[1]);

        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
            return true;
        }

        switch (args[2].toUpperCase()) {
        case "CONQUEST":
            faction = new ConquestFaction(args[1]);
            break;
        case "KOTH":
            faction = new KothFaction(args[1]);
            break;
        default:
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        plugin.getFactionManager().createFaction(faction, sender);
        sender.sendMessage(ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully(args[2]) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            return Collections.emptyList();
        }

        EventType[] eventTypes = EventType.values();
        List<String> results = new ArrayList<>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            results.add(eventType.name());
        }

        return results;
    }
}
