package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CobbleCommand implements CommandExecutor {

    private HCF plugin;

    public static Set<UUID> players = new HashSet<>();

    public CobbleCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission(command.getPermission())){
                if(players.contains(p.getUniqueId())){
                    players.remove(p.getUniqueId());
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("COBBLE-PICKUP-ENABLED")));
                    return true;
                }
                    players.add(p.getUniqueId());
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("COBBLE-PICKUP-DISABLED")));
                    return true;
            }
        }
        sender.sendMessage(Utils.chat("&cOnly players may use this command."));

        return false;
    }
}
