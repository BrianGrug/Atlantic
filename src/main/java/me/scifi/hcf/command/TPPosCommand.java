package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPPosCommand implements CommandExecutor {

    private HCF plugin;

    public TPPosCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 3) {
                try {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);
                    Location loc = new Location(p.getWorld(), x, y, z);
                    p.teleport(loc);
                    p.sendMessage(Utils.chat(plugin.messagesYML.getString("TELEPORTED-TO-COORDS")
                            .replace("%x%", args[0])
                            .replace("%y%", args[1])
                            .replace("%z%", args[2])));
                    return true;
                } catch (NumberFormatException e){
                    sender.sendMessage(Utils.chat("&cUsage: /tppos <x> <y> <z> | /tppos <player> <x> <y> <z>"));
                }
            }

            if(args.length == 4){
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if(target != null){
                    try {
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        int z = Integer.parseInt(args[3]);
                        Location loc = new Location(p.getWorld(), x, y, z);
                        target.teleport(loc);
                        p.sendMessage(Utils.chat(plugin.messagesYML.getString("TELEPORTED-TARGET-TO-COORDS")
                                .replace("%x%", args[1])
                                .replace("%y%", args[2])
                                .replace("%z%", args[3])
                                .replace("%target%", target.getName())));
                        return true;
                    } catch(NumberFormatException e){
                        sender.sendMessage(Utils.chat("&cUsage: /tppos <x> <y> <z> | /tppos <player> <x> <y> <z>"));
                    }
                    return true;
                }

                p.sendMessage(Utils.chat("&cPlayer is not online."));
                return true;
            }
            sender.sendMessage(Utils.chat("&cUsage: /tppos <x> <y> <z> | /tppos <player> <x> <y> <z>"));
            return false;
        }
        return false;
    }
}
