package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {

    private HCF plugin;

    public RenameCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission(command.getPermission())) {
                if (args.length == 0) {
                    p.sendMessage(Utils.chat("&cUsage: /rename <name>"));
                    return true;
                }
                    if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)) {
                        ItemMeta meta = p.getItemInHand().getItemMeta();
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0; i < args.length; i ++){
                            sb.append(args[i]).append(" ");
                        }
                        meta.setDisplayName(Utils.chat(sb.toString()));
                        p.getItemInHand().setItemMeta(meta);
                        p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("ITEM-RENAMED")));
                        return true;
                    } else {
                        p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("ITEM-UNRENAMABLE")));
                        return true;
                    }
                }
            }


        sender.sendMessage(Utils.chat("&cOnly players may execute this command."));
        return false;
    }
}
