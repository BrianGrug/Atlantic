package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    private HCF plugin;

    public HealCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(command.getPermission())){
            if(args.length == 0){
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    p.setSaturation(20f);
                    sender.sendMessage(Utils.chat(plugin.getMessagesYML().getString("SELF-HEALED")));
                    return true;
                }
                sender.sendMessage(Utils.chat("&cYou Can Only Heal A Player."));
                return true;
            }
            if(sender.hasPermission(command.getPermission() + ".others")) {
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if (target != null) {
                    target.setHealth(20);
                    target.setFireTicks(0);
                    target.setFoodLevel(20);
                    target.setSaturation(20f);
                    sender.sendMessage(Utils.chat(plugin.getMessagesYML().getString("OTHER-HEALED")
                    .replace("%player%",target.getName())));
                    return true;
                }
                sender.sendMessage(Utils.chat("&cPlayer does not exist."));
                return true;
            }
        }
        return false;
    }
}
