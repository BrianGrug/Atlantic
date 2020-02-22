package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FreezeCommand implements CommandExecutor {

    private HCF plugin;

    public static Map<UUID, Location> frozen = new HashMap<>();

    public FreezeCommand(HCF plugin){
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
                if(!frozen.containsKey(target.getUniqueId())){
                    frozen.put(target.getUniqueId(),target.getLocation());
                    sender.sendMessage(Utils.chat(plugin.messagesYML.getString("STAFF-FROZEN-PLAYER")
                    .replace("%player%", target.getName())));
                    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if(frozen.containsKey(target.getUniqueId())){
                                List<String> frozen_msg = plugin.messagesYML.getStringList("PLAYER-FROZEN-MESSAGE");
                                Utils.list(frozen_msg).forEach(target::sendMessage);
                            }
                        }
                    },0,100);
                    return true;
                }

                frozen.remove(target.getUniqueId());
                sender.sendMessage(Utils.chat(plugin.messagesYML.getString("STAFF-UNFROZEN-PLAYER")
                .replace("%player%",target.getName())));
                return true;
            }
            sender.sendMessage(Utils.chat(plugin.messagesYML.getString("PLAYER-NOT-FOUND")));
            return true;
        }
        return false;
    }
}
