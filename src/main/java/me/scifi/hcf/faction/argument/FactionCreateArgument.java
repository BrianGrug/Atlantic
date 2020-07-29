package me.scifi.hcf.faction.argument;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;

/**
 * Faction argument used to create a new {@link Faction}.
 */
public class FactionCreateArgument extends CommandArgument {

    private final HCF plugin;

    public FactionCreateArgument(HCF plugin) {
        super("create", "Create a faction.", new String[] { "make", "define" });
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <factionName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        String name = args[1];

        if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is a blocked faction name.");
            return true;
        }

        if (name.length() < ConfigurationService.FACTION_NAME_CHARACTERS_MIN) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + ConfigurationService.FACTION_NAME_CHARACTERS_MIN + " characters.");
            return true;
        }

        if (name.length() > ConfigurationService.FACTION_NAME_CHARACTERS_MAX) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + ConfigurationService.FACTION_NAME_CHARACTERS_MAX + " characters.");
            return true;
        }

        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }

        if (plugin.getManagerHandler().getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
            return true;
        }

        if (plugin.getManagerHandler().getFactionManager().getPlayerFaction((Player) sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }

        plugin.getManagerHandler().getFactionManager().createFaction(new PlayerFaction(name), sender);
        return true;
    }
}
