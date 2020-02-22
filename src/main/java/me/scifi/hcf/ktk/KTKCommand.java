package me.scifi.hcf.ktk;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KTKCommand implements CommandExecutor {

    private HCF plugin;

    public KTKCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("ktk")){
          if(sender.hasPermission(command.getPermission())){
              if(args.length == 0){

                  List<String> ktk = plugin.messagesYML.getStringList("KING-HELP");
                  ktk.forEach(str -> sender.sendMessage(Utils.chat(str)));
                  return true;
              }
              if(args.length == 1) {
                  if(args[0].equalsIgnoreCase("status")){
                      List<String> status = plugin.messagesYML.getStringList("KING-STATUS");
                      if(plugin.kingManager.isEventActive()){
                          String health = Double.toString(plugin.kingManager.getKingPlayer().getHealth());
                          status.forEach(str -> sender.sendMessage(Utils.chat(str)
                          .replace("%KINGX%",Integer.toString(plugin.kingManager.getKingPlayer().getLocation().getBlockX()))
                          .replace("%KINGY%",Integer.toString(plugin.kingManager.getKingPlayer().getLocation().getBlockY()))
                          .replace("%KINGZ%",Integer.toString(plugin.kingManager.getKingPlayer().getLocation().getBlockZ())
                          .replace("%HEALTH%",health)
                          .replace("%KING%",plugin.kingManager.getKingPlayer().getName()))));
                          return true;
                      } else {
                          sender.sendMessage(Utils.chat(plugin.messagesYML.getString("KING-NOT-FOUND")));
                          return true;
                      }
                  } else if(args[0].equalsIgnoreCase("end")){

                    if(sender.hasPermission(command.getPermission() + ".end")) {
                        if (plugin.kingManager.isEventActive()) {
                            sender.sendMessage(Utils.chat(plugin.messagesYML.getString("KING-EVENT-ENDED")));
                            List<String> ktkend = plugin.messagesYML.getStringList("KING-FORCE-END");
                            ktkend.forEach(str -> Bukkit.getServer().broadcastMessage(Utils.chat(str)));
                            plugin.kingManager.removeKing(true);
                        } else {
                            sender.sendMessage(Utils.chat(plugin.messagesYML.getString("KING-NOT-FOUND")));
                            return true;
                        }
                    }
                  } else {
                      if (sender.hasPermission(command.getPermission() + ".start")) {
                          Player p = Bukkit.getServer().getPlayer(args[0]);
                          if(p != null){
                              if(plugin.kingManager.getKingPlayer() != null){
                                  sender.sendMessage(Utils.chat(plugin.messagesYML.getString("KING-EVENT-ACTIVE")));
                                  return true;
                              } else {
                                  sender.sendMessage(Utils.chat(plugin.messagesYML.getString("KING-EVENT-STARTED")));
                                  plugin.kingManager.setKingPlayer(p);
                              return true;
                              }
                          } else {
                              sender.sendMessage(Utils.chat("&cPlayer Does Not Exist"));
                          }
                      }
                  }
              }

          }
      }
        return false;
    }
}
