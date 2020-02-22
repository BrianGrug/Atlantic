package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    private HCF plugin;

    public TeleportCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(command.getPermission())) {
                if (args.length == 0) {
                    sender.sendMessage(Utils.chat("&cUsage: /teleport <player> | <player> <target>"));
                    return true;
                }

                if (args.length == 1) {
                    Player target = Bukkit.getServer().getPlayer(args[0]);
                    if (target != null) {
                        p.teleport(target);
                        p.sendMessage(Utils.chat(plugin.messagesYML.getString("TELEPORTED-TO-TARGET")
                                .replace("%player%", target.getName())));
                        return true;
                    }
                    p.sendMessage(Utils.chat("&cPlayer is not online."));
                    return true;
                }

                if (args.length == 2) {
                    Player teleport = Bukkit.getServer().getPlayer(args[0]);
                    Player teleportTo = Bukkit.getServer().getPlayer(args[1]);
                    if (teleport != null && teleportTo != null) {
                        teleport.teleport(teleportTo);
                        p.sendMessage(Utils.chat(plugin.messagesYML.getString("TELEPORTED-PLAYER-TO-TARGET")
                                .replace("%player%", teleport.getName())
                                .replace("%target%", teleportTo.getName())));
                        return true;
                    }
                    sender.sendMessage(Utils.chat("&cBoth players must be online."));
                }
            }
        }
        return false;
    }
}
