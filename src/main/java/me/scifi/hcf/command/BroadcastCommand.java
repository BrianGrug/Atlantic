package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

    private HCF plugin = HCF.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(command.getPermission())){
            if(args.length < 1){
                sender.sendMessage(Utils.chat("&cUsage: /broadcast <message>"));
                return true;
            }
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < args.length; i++){
                sb.append(args[i]).append(" ");
            }
            Bukkit.getServer().broadcastMessage(Utils.chat(sb.toString()));
        }
        return false;
    }
}
