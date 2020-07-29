package me.scifi.hcf.command;

import me.scifi.hcf.Cooldown;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReportCommand implements CommandExecutor {

    private HCF plugin;

    private Cooldown report = new Cooldown();

    public ReportCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(sender instanceof Player){
          Player p = (Player) sender;
          if(p.hasPermission(command.getPermission())) {
              if (args.length == 0) {
                  sender.sendMessage(Utils.chat("&cUsage: /" + label + " [player] [reason]"));
                  return true;
              }
              if (args.length == 1) {
                  sender.sendMessage(Utils.chat("&cYou must specify a reason for your report"));
                  return true;
              }
              Player reported = Bukkit.getServer().getPlayer(args[0]);
            if(!report.isOnCooldown(p)) {
                if (reported != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String reason = sb.toString();
                    for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                        if (staff.hasPermission(command.getPermission() + ".recieve")) {
                            List<String> msg = Utils.list(plugin.getMessagesYML().getStringList("REPORT-MESSAGE"));
                            msg.forEach(str -> staff.sendMessage(str
                                    .replace("%reported%", reported.getName())
                                    .replace("%reason%", reason)
                                    .replace("%reporter%", p.getName())));
                        }
                    }
                    report.placeOnCooldown(p, 3600);
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("REPORT-RECIEVED")));
                    return true;
                }
                p.sendMessage(Utils.chat("&cPlayer is not online."));
                return true;
            }
          }

          }
        sender.sendMessage(Utils.chat("&cOnly players may execute this command."));
        return false;
    }
}
