package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private HCF plugin;

    public SetSpawnCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("setspawn")){
          if(sender instanceof Player){
              Player p = (Player) sender;
              if(p.hasPermission(command.getPermission())){
                  int x = p.getLocation().getBlockX();
                  int y = p.getLocation().getBlockY();
                  int z = p.getLocation().getBlockZ();
                  Bukkit.getServer().getWorlds().get(0).setSpawnLocation(x,y,z);
                  p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("SET-SPAWN-MESSAGE")));
                  return true;
              }
          } else {
              sender.sendMessage(Utils.chat("&cOnly Players May Execute This Command"));
              return true;
          }
      }
        return false;
    }
}
