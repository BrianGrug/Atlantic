package me.scifi.hcf.command;

import com.doctordark.util.Rank;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {

    public static Map<UUID, UUID> lastMessage = new HashMap<>();
    private HCF plugin;

    public MessageCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(command.getPermission())) {
                if (args.length == 0) {
                    p.sendMessage(Utils.chat("&cYou must specify a player to message."));
                    return true;
                }
                if (args.length == 1) {
                    p.sendMessage(Utils.chat("&cYou must specify a message to send."));
                    return true;
                }
                Player target = Bukkit.getServer().getPlayer(args[0]);

                if (target != null) {
                    String prefix = plugin.getRank().getGroupPrefix(p);
                    String otherPrefix = plugin.getRank().getGroupPrefix(target);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String message = sb.toString();
                    target.sendMessage(Utils.chat("&7(From " + prefix + p.getName() + "&7) &f" + message));
                    lastMessage.put(p.getUniqueId(), target.getUniqueId());
                    p.sendMessage(Utils.chat("&7(To " + otherPrefix + target.getName() + "&7) &f" + message));
                    return true;
                }
                p.sendMessage(Utils.chat("&cPlayer is not online."));
                return true;
            }
        }
        return false;
    }
}
