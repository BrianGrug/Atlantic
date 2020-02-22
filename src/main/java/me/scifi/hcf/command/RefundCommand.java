package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.listener.DeathListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class RefundCommand implements CommandExecutor {

    private HCF plugin;

    public RefundCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length == 0){
                sender.sendMessage(Utils.chat("&cYou must specify a player."));
                return true;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if(p.hasPermission(command.getPermission())){
                if(DeathListener.contents.containsKey(target.getUniqueId()) && DeathListener.armor.containsKey(target.getUniqueId())){
                    target.getInventory().setContents(DeathListener.contents.get(target.getUniqueId()));
                    DeathListener.contents.remove(target.getUniqueId());
                    target.getInventory().setArmorContents(DeathListener.armor.get(target.getUniqueId()));
                    DeathListener.armor.remove(target.getUniqueId());
                    return true;
                }
                p.sendMessage(Utils.chat("&cThis Player Has Not Died Recently."));
                return true;
            }
        }
        return false;
    }
}
