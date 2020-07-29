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

public class RequestCommand implements CommandExecutor {

    private HCF plugin;

    private Cooldown request = new Cooldown();

    public RequestCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(command.getPermission())) {
                if (args.length == 0) {
                    sender.sendMessage(Utils.chat("&cUsage&7: &c/" + label + " [message]"));
                    return true;
                }
                if(!request.isOnCooldown(p)) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String message = sb.toString();
                    for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
                        if (staff.hasPermission(command.getPermission() + ".recieve")) {
                            List<String> msg = Utils.list(plugin.getMessagesYML().getStringList("REQUEST-MESSAGE"));
                            msg.forEach(str -> staff.sendMessage(str
                                    .replace("%player%", sender.getName())
                                    .replace("%message%", message)));
                        }
                    }

                    request.placeOnCooldown(p, 3600);
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("REQUEST-RECIEVED")));
                    return true;
                }
            }
            sender.sendMessage(Utils.chat("&cOnly players may use this command."));
        }
        return false;
    }
}
