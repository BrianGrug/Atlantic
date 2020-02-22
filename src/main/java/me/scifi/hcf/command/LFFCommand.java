package me.scifi.hcf.command;

import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LFFCommand implements CommandExecutor {

    private HCF plugin;

    public LFFCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("lff")){
          if(sender instanceof Player){
              Player p = (Player) sender;
              FactionManager fm = plugin.getFactionManager();
              if(p.hasPermission(command.getPermission())){
                  if(fm.getPlayerFaction(p) == null){
                      List<String> lff_message = plugin.messagesYML.getStringList("LFF-MESSAGE");
                      if(plugin.getLff().isOnCooldown(p)) {
                          p.sendMessage(Utils.chat("&cYou are already on cooldown for " + DurationFormatter.getRemaining(plugin.getLff().getRemaining(p),true)));
                          return true;
                      } else {
                          lff_message.forEach(str -> Bukkit.getServer().broadcastMessage(Utils.chat(str)
                                  .replace("%PLAYER%",p.getName())));
                          plugin.getLff().placeOnCooldown(p, plugin.getConfig().getInt("LFF-COOLDOWN"));
                      }
                      return true;
                  } else {
                      p.sendMessage(Utils.chat("&cYou are already in a faction."));
                      return true;
                  }
              }
          } else {
              sender.sendMessage(Utils.chat("&cOnly Players May Use This Command."));
          }
      }
        return false;
    }
}
