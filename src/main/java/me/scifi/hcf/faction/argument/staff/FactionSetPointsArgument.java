package me.scifi.hcf.faction.argument.staff;

import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.FactionManager;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionSetPointsArgument extends CommandArgument {

    private HCF plugin;

    public FactionSetPointsArgument(HCF plugin) {
        super("setpoints", "Set a factions points manually");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + getName();

    }

    @Override
    public String getUsage(String label) {
        return "&c/f setpoints <faction> <points>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            sender.sendMessage(Utils.chat(getUsage(label)));
            return true;
        }

        FactionManager fm = HCF.getPlugin().getFactionManager();
        if(fm.getFaction(args[1]) == null){
            sender.sendMessage(Utils.chat("&cFaction not found."));
            return true;
        }

        if(!(fm.getFaction(args[1]) instanceof PlayerFaction)){
            sender.sendMessage(Utils.chat("&cEvent Factions do not contain points."));
            return true;
        }

        Faction faction = fm.getFaction(args[1]);
        long amount = 0;
        try {
           amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e){
            sender.sendMessage(Utils.chat("&c" + args[2] + " is an invalid amount."));
        }
        PlayerFaction playerFaction = (PlayerFaction) faction;
        playerFaction.setPoints(amount);
        sender.sendMessage(Utils.chat("&eYou have set &f" + playerFaction.getName() + "'s &epoints to &f" + amount));
        return false;
    }
}
