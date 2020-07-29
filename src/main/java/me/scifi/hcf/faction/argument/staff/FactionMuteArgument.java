package me.scifi.hcf.faction.argument.staff;

import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.FactionMember;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FactionMuteArgument extends CommandArgument {

    private final HCF plugin;

    public FactionMuteArgument(HCF plugin) {
        super("mute", "Mutes a whole faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " <faction>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }
        if (plugin.getManagerHandler().getFactionManager().getFaction(args[1]) == null) {
            sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        Faction faction = plugin.getManagerHandler().getFactionManager().getFaction(args[1]);
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "This is not a player created faction!");
            return true;
        }
        PlayerFaction playerFaction = (PlayerFaction) faction;

        playerFaction.getOnlinePlayers().forEach(player -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HCF.getPlugin().getConfig().getString("FACTION-PUNISHMENTS.mute")
                .replace("%player%", player.getName())));

        return true;
    }
}
