package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.FactionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChestCommand implements CommandExecutor {

    private HCF plugin;

    public ChestCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            FactionManager fm = plugin.getFactionManager();
            if(p.hasPermission(command.getPermission())){
                if(plugin.getConfig().getBoolean("kit-map")){
                    if(fm.getClaimAt(p.getLocation()) != null && fm.getClaimAt(p.getLocation()).getFaction().isSafezone()) {
                        p.openInventory(p.getEnderChest());
                        return true;
                    } else {
                        p.sendMessage(Utils.chat("&cThis command can only be used in spawn."));
                        return true;
                    }
                }
                p.sendMessage(Utils.chat("&cThis command only works on kit-map."));
                return true;
            }
        }
        sender.sendMessage(Utils.chat("&cOnly players may use this command."));
        return false;
    }
}
