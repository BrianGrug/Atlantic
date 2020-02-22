package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {

    private HCF plugin;

    public GameModeCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
           Player p = (Player) sender;
            if(p.hasPermission(command.getPermission())){
                if(args.length == 0) {
                    sender.sendMessage(Utils.chat("&cUsage: /gamemode <mode> | /gamemode <mode> <player>"));
                    return true;
                }

                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative"))
                    {
                        p.sendMessage(Utils.chat(plugin.messagesYML.getString("GAMEMODE-CHANGED-CREATIVE")));
                        p.setGameMode(GameMode.CREATIVE);
                        return true;

                    } else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival"))
                    {
                        p.sendMessage(Utils.chat(plugin.messagesYML.getString("GAMEMODE-CHANGED-SURVIVAL")));
                        p.setGameMode(GameMode.SURVIVAL);
                        return true;
                    }
                    sender.sendMessage(Utils.chat("&cUsage: /gamemode <mode> | /gamemode <mode> <player>"));
                    return true;
                }

                if(args.length == 2){
                    Player target = Bukkit.getServer().getPlayer(args[1]);
                    if(target != null){
                        if(args[0].equalsIgnoreCase("&c") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative"))
                        {
                            p.sendMessage(Utils.chat(plugin.messagesYML.getString("GAMEMODE-CHANGED-CREATIVE")));
                            target.setGameMode(GameMode.CREATIVE);
                            return true;
                        } else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival"))
                        {
                            p.sendMessage(Utils.chat(plugin.messagesYML.getString("GAMEMODE-CHANGED-SURVIVAL")));
                            target.setGameMode(GameMode.SURVIVAL);
                            return true;
                        }
                        sender.sendMessage(Utils.chat("&cUsage: /gamemode <mode> | /gamemode <mode> <player>"));
                        return true;
                    }
                    sender.sendMessage(Utils.chat("&cPlayer is not online."));
                    return true;
                }
                sender.sendMessage(Utils.chat("&cUsage: /gamemode <mode> | /gamemode <mode> <player>"));
            }
        }
        return false;
    }
}
