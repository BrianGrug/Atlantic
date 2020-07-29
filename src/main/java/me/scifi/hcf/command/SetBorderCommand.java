package me.scifi.hcf.command;

import com.doctordark.util.Config;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBorderCommand implements CommandExecutor {

    private HCF plugin;

    public SetBorderCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("setborder")){
          if(sender instanceof Player){
              Player p = (Player) sender;
              if(p.hasPermission(command.getPermission())){
                  if(args.length == 0){
                      p.sendMessage(Utils.chat("&c/" + label + " NORMAL|NETHER|END (Size)"));
                      return true;
                  }
                  if(args.length == 1){
                      p.sendMessage(Utils.chat("&c/" + label + " NORMAL|NETHER|END (Size)"));
                      return true;
                  }
                  if(args.length == 2){
                      String world = args[0];
                      int size = Integer.parseInt(args[1]);
                      if(world.equalsIgnoreCase("normal")){
                          ConfigurationService.config.set("border_sizes.NORMAL", size);
                          ConfigurationService.config.save();
                          p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("BORDER-NORMAL-SET")));
                          return true;
                      } else if(world.equalsIgnoreCase("nether")){
                          ConfigurationService.config.set("border_sizes.NETHER", size);
                          ConfigurationService.config.save();
                          p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("BORDER-NETHER-SET")));
                          return true;
                      } else if(world.equalsIgnoreCase("end")){
                          ConfigurationService.config.set("border_sizes.END", size);
                          ConfigurationService.config.save();
                          p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("BORDER-END-SET")));
                          return true;

                      }
                  }
              }
          }
      }
        return false;
    }
}
