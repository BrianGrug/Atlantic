package me.scifi.hcf.reclaims;

import com.doctordark.util.Rank;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReclaimCommand implements CommandExecutor {

    private HCF plugin;

    public ReclaimCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission(command.getPermission())) {
                String rank = plugin.getRank().getGroupName(p);
                if (HCF.getPlugin().getManagerHandler().getReclaimManager().hasReclaimed(p)) {
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("RECLAIM-ALREADY-RECLAIMED")));
                    return true;
                }
                if (HCF.getPlugin().getManagerHandler().getReclaimManager().hasReclaim(rank)) {
                    p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("RECLAIM-USED-RECLAIM")));
                    HCF.getPlugin().getManagerHandler().getReclaimManager().setReclaimed(p);
                    List<String> commands = HCF.getPlugin().getManagerHandler().getReclaimManager().returnReclaim(rank);
                    commands.forEach(str -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), str
                            .replace("%player%", p.getName())
                            .replace("%rank%", rank)));
                    return true;
                }

                p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("RECLAIM-NOT-FOUND")));
                return true;
            }
            sender.sendMessage(Utils.chat("&cOnly players may execute this command."));
        }
        return false;
    }
}
