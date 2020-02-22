package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlightCommand implements CommandExecutor {

    private HCF plugin;

    public FlightCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission(command.getPermission())) {
                if (args.length < 1) {
                    if (p.getAllowFlight()) {
                        p.setAllowFlight(false);
                        p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("FLIGHT-DISABLED")));
                        return true;
                    }
                    p.setAllowFlight(true);
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("FLIGHT-ENABLED")));
                    return true;
                }
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if(target != null) {
                    if(target.getAllowFlight()){
                        target.setAllowFlight(false);
                        target.sendMessage(Utils.chat(plugin.getMessagesYML().getString("FLIGHT-DISABLED-OTHERS").replace("%player%", target.getName())));
                        return true;
                    }
                        target.setAllowFlight(true);
                        target.sendMessage(Utils.chat(plugin.getMessagesYML().getString("FLIGHT-ENABLED-OTHERS").replace("%player%", target.getName())));
                        return true;
                }
            }
        }
        return false;
    }
}
