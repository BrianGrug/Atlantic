package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.staffmode.Vanish;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private HCF plugin;

    public VanishCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("vanish")){
          if(sender instanceof Player){
              Player p = (Player) sender;
              if(p.hasPermission(command.getPermission())){
                  if(Vanish.isPlayerVanished(p)){
                      Vanish.disableVanish(p);
                      p.sendMessage(Utils.chat(plugin.messagesYML.getString("VANISH-DISABLED")));
                      return true;
                  } else {
                      Vanish.setVanished(p);
                      p.sendMessage(Utils.chat(plugin.messagesYML.getString("VANISH-ENABLED")));
                  }
              }
          }
      }
        return false;
    }
}
