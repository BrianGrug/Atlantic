package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TeleportHereCommand implements CommandExecutor {

    private HCF plugin;

    public TeleportHereCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission(command.getPermission())){
                if(args.length == 0){
                    sender.sendMessage(Utils.chat("&cUsage: /teleporthere <player>"));
                    return true;
                }
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if(target != null){
                    target.teleport(p);
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("TELEPORT-HERE-MESSAGE")
                    .replace("%player%", target.getName())));
                    return true;
                }
                sender.sendMessage(Utils.chat("&cPlayer is not online."));
            }
        }
        return false;
    }
}
