package me.scifi.hcf.reclaims;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetReclaimCommand implements CommandExecutor {

    private HCF plugin;

    public ResetReclaimCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(command.getPermission())){
            if(args.length == 0){
                sender.sendMessage(Utils.chat("&cYou must specify a player."));
                return true;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if(target != null){
                if(HCF.getPlugin().getReclaimManager().hasReclaimed(target)){
                    HCF.getPlugin().getReclaimManager().resetReclaims(target);
                    sender.sendMessage(Utils.chat(plugin.messagesYML.getString("RECLAIM-SUCCESSFULLY-RESET")
                    .replace("%player%",target.getName())));
                    return true;
                }
                sender.sendMessage(Utils.chat(plugin.messagesYML.getString("RECLAIM-PLAYER-CANRECLAIM")));
                return true;
            }
            sender.sendMessage(Utils.chat("&cPlayer is not online."));
            return true;
        }
        return false;
    }
}
