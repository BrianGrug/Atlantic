package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private HCF plugin;

    public SpawnCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("spawn")){
          if(sender instanceof Player){
              Player p = (Player) sender;
              if(p.hasPermission(command.getPermission())){
                  p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                  p.getLocation().setYaw(plugin.getConfig().getFloat("SPAWN.YAW"));
                  p.getLocation().setPitch(plugin.getConfig().getFloat("SPAWN.PITCH"));
                  p.sendMessage(Utils.chat(plugin.messagesYML.getString("SPAWN-MESSAGE")));
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
