package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand implements CommandExecutor {

    private HCF plugin;

    public HelpCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("help")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(command.getPermission())){
                    List<String> message = plugin.messagesYML.getStringList("HELP-MESSAGE");
                    message.forEach(str -> p.sendMessage(Utils.chat(str)));
                }
            }
        }
        return false;
    }
}
