package me.scifi.hcf.command;

import com.doctordark.util.Rank;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    private HCF plugin;

    public ReplyCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(command.getPermission())) {
                if (args.length < 1) {
                    sender.sendMessage(Utils.chat("&cUsage: /r <message>"));
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    sb.append(args[i]).append(" ");

                }
                String message = sb.toString();
                if (!MessageCommand.lastMessage.containsKey(p.getUniqueId())) {
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("CONVERSATION-NOT-FOUND")));
                    return true;
                }
                Player target = Bukkit.getServer().getPlayer(MessageCommand.lastMessage.get(p.getUniqueId()));
                String prefix = plugin.getRank().getGroupPrefix(p);
                String otherPrefix = "";
                if (target != null) {
                    otherPrefix = plugin.getRank().getGroupPrefix(target);
                    target.sendMessage(Utils.chat("&7(From " + prefix + p.getName() + "&7) &f" + message));
                    MessageCommand.lastMessage.put(p.getUniqueId(), target.getUniqueId());
                    p.sendMessage(Utils.chat("&7(To " + otherPrefix + target.getName() + "&7) &f" + message));
                    return true;
                }
                p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("REPLY-PLAYER-LOGGED-OUT")));
                MessageCommand.lastMessage.remove(p.getUniqueId());
                return true;
            }
        }
        return false;
    }
}
