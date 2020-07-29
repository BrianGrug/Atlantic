package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CoordinatesCommand implements CommandExecutor {

    private HCF plugin;

    public CoordinatesCommand(HCF plugin){
        this.plugin  = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission(command.getPermission())){
                List<String> msg = Utils.list(plugin.getMessagesYML().getStringList("COORDNATES-MESSAGE"));
                msg.forEach(p::sendMessage);
                return true;
            }
        }
        sender.sendMessage(Utils.chat("&cOnly players may use this command."));
        return true;
    }
}
