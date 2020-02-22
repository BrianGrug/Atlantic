package me.scifi.hcf.command;

import com.doctordark.util.BukkitUtils;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {

    private HCF plugin;

    public TopCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.chat("&cOnly players may use this command."));
            return true;
        }
        Player player = (Player)sender;
        Location original = player.getLocation();
        Location highestLocation = BukkitUtils.getHighestLocation(original);
        if (highestLocation != null && !highestLocation.equals(original)) {
            Block originBlock = original.getBlock();
            if ((highestLocation.getBlockY() - originBlock.getY() != 1 || originBlock.getType() != Material.WATER) && originBlock.getType() != Material.STATIONARY_WATER) {
                player.teleport(highestLocation.add(0.0, 1.0, 0.0));
                sender.sendMessage(Utils.chat("&eTeleported to highest location."));
                return true;
            }
        }
        sender.sendMessage(Utils.chat("&cCould not find a higher location."));
        return true;
    }
}

