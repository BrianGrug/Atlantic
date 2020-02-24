package me.scifi.hcf.faction.argument.staff;

import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.inventories.Inventories;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class FactionPunishArgument extends CommandArgument {

    private final HCF plugin;

    public FactionPunishArgument(HCF plugin) {
        super("punish", "Punish a whole faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " <faction>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws IOException {

        if(!(sender instanceof Player)){
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }
        if (plugin.getFactionManager().getFaction(args[1]) == null) {
            sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        Faction faction = plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "This is not a player created faction!");
            return true;
        }

        Player player = (Player) sender;

        PlayerFaction playerFaction = (PlayerFaction) faction;

        Inventories.punishFactionInventory(player, playerFaction);

        return true;
    }
}
