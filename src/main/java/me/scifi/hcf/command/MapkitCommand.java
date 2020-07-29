package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MapkitCommand implements CommandExecutor {

    private HCF plugin;

    public MapkitCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("mapkit")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(command.getPermission())){
                    List<String> mapkitMessage = plugin.getMessagesYML().getStringList("MAPKIT-MESSAGE");
                    mapkitMessage.forEach(str -> p.sendMessage(Utils.chat(str)));
                    return true;
                }
            } else {
                sender.sendMessage(Utils.chat("&cOnly Players May Execute This Command."));
                return true;
            }
        }
        return false;
    }
}
