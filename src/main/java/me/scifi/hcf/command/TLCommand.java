package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TLCommand implements CommandExecutor {

    private HCF plugin;

    public TLCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("tl")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(command.getPermission())){
                    int x = p.getLocation().getBlockX();
                    int y = p.getLocation().getBlockY();
                    int z = p.getLocation().getBlockZ();
                    p.chat("/f message [ " + x + ", " + y + ", " + z + " ]");
                }
            }
        }
        return false;
    }
}
