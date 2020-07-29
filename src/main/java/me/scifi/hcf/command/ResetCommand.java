package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor {

    private HCF plugin;

    public ResetCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(command.getPermission())){
            if(args.length == 0){
                sender.sendMessage(Utils.chat("&cUsage: /reset <player>"));
                return true;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if(target != null){
                plugin.getManagerHandler().getUserManager().getUserAsync(target.getUniqueId()).setKills(0);
                plugin.getManagerHandler().getUserManager().getUserAsync(target.getUniqueId()).setDeaths(0);
                sender.sendMessage(Utils.chat(plugin.getMessagesYML().getString("PLAYER-STATISTICS-RESET")
                .replace("%player%",target.getName())));
                return true;
            }
            sender.sendMessage(Utils.chat("&cPlayer is not online."));
        }
        return false;
    }
}
