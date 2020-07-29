package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertCommand implements CommandExecutor {

    private HCF plugin;

    public AlertCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("alert")) {
            if (sender.hasPermission(command.getPermission())) {

                if (args.length >= 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String message = sb.toString();
                    String prefix = HCF.getPlugin().getMessagesYML().getString("ALERT-PREFIX");
                    Bukkit.getServer().broadcastMessage(Utils.chat(prefix + " " + message));
                    return true;
                }

                sender.sendMessage(Utils.chat("&cYou Must Specify A Message To Broadcast"));
                return true;
            }
        }
        return false;
    }
}
