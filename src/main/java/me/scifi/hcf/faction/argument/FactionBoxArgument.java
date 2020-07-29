package me.scifi.hcf.faction.argument;

import com.doctordark.util.command.CommandArgument;
import com.doctordark.util.cuboid.Cuboid;
import com.doctordark.util.cuboid.CuboidDirection;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class FactionBoxArgument extends CommandArgument {

    private HCF plugin;

    private Set<UUID> redeemed = new HashSet<>();


    public FactionBoxArgument(HCF plugin){
        super("box", "create a box around your claim");
        this.plugin = plugin;
        permission = "hcf.command.faction.argument." + getName();

    }

    @Override
    public String getUsage(String label) {
        return Utils.chat("&cUsage: /" + label + " " + getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(Utils.chat("&cOnly players may execute this command."));
            return true;
        }

        Player p = (Player) sender;

        if(plugin.getManagerHandler().getFactionManager().getPlayerFaction(p) == null){
            p.sendMessage(Utils.chat("&cYou must be in a faction to execute this command."));
            return true;
        }

        if(plugin.getManagerHandler().getFactionManager().getPlayerFaction(p).getClaims().isEmpty()){
            p.sendMessage(Utils.chat("&cYou must have a claim to execute this command."));
            return true;
        }

        if(redeemed.contains(plugin.getManagerHandler().getFactionManager().getPlayerFaction(p).getUniqueID())){
            p.sendMessage(Utils.chat("&cYour faction has already used this command."));
            return true;
        }
        plugin.getManagerHandler().getFactionManager().getPlayerFaction(p).getClaims().forEach(claim -> {
            int y = 80;
            while (y > 0){
                Cuboid cuboid = new Cuboid(new Location(claim.getWorld(), claim.getX1(),claim.getY1(),claim.getZ1()), new Location(claim.getWorld(), claim.getX2(), claim.getY2(), claim.getZ2()));
                //Side One
                Iterator<Block> north = cuboid.getFace(CuboidDirection.NORTH).iterator();
                Iterator<Block> south = cuboid.getFace(CuboidDirection.SOUTH).iterator();
                Iterator<Block> east = cuboid.getFace(CuboidDirection.EAST).iterator();
                Iterator<Block> west = cuboid.getFace(CuboidDirection.WEST).iterator();
                while (north.hasNext()){
                    Block block = north.next();
                    if(block.getY() > 90 || block.getY() < 1) continue;
                    block.setType(Material.DIRT);
                }
                while (south.hasNext()){
                    Block block = south.next();
                    if(block.getY() > 90 || block.getY() < 1) continue;
                    block.setType(Material.DIRT);
                }
                while (east.hasNext()){
                    Block block = east.next();
                    if(block.getY() > 90 || block.getY() < 1) continue;
                    block.setType(Material.DIRT);
                }
                while (west.hasNext()){
                    Block block = west.next();
                    if(block.getY() > 90 || block.getY() < 1) continue;
                    block.setType(Material.DIRT);
                }
                y--;
            }
        });
        redeemed.add(plugin.getManagerHandler().getFactionManager().getPlayerFaction(p).getUniqueID());

        return false;
    }
}
