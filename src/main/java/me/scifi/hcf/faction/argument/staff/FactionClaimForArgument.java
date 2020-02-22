package me.scifi.hcf.faction.argument.staff;

import com.doctordark.util.command.CommandArgument;
import com.doctordark.util.cuboid.Cuboid;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.ClaimableFaction;
import me.scifi.hcf.faction.type.Faction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to claim land for other {@link ClaimableFaction}s.
 */
public class FactionClaimForArgument extends CommandArgument {
    private static final int MIN_EVENT_CLAIM_AREA;


    private final HCF plugin;

    public FactionClaimForArgument(HCF plugin) {
        super("claimfor", " Claim for other factions");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factioName> [shouldClearClaims]";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = plugin.getFactionManager().getFaction(args[1]);
        if(!(faction instanceof ClaimableFaction)){
            sender.sendMessage(ChatColor.RED + "This is not a claimable faction (cannot contain claims)");
            return true;
        }
        if(faction instanceof PlayerFaction){
            sender.sendMessage(ChatColor.RED + "You cannot claim player factions like this. Try /f claim wand");
            return true;
        }
        final WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
        if(worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set event claim areas.");
            return true;
        }
        final Player player = (Player) sender;
        final Selection selection = worldEditPlugin.getSelection(player);
        if(selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if(selection.getWidth() < MIN_EVENT_CLAIM_AREA || selection.getLength() < MIN_EVENT_CLAIM_AREA) {
            sender.sendMessage(ChatColor.RED + "Event claim areas must be at least " + MIN_EVENT_CLAIM_AREA + 'x' + MIN_EVENT_CLAIM_AREA + '.');
            return true;
        }
        ClaimableFaction claimableFaction = (ClaimableFaction) faction;
        if(args.length == 3 && (args[2].toLowerCase().contains("true") || args[2].toLowerCase().contains("yes")) && ((ClaimableFaction) faction).getClaims().size() > 0){
            sender.sendMessage(ChatColor.YELLOW + "Set claim for " + faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
            for(Claim claim : claimableFaction.getClaims()){
                claimableFaction.removeClaim(claim, sender);
            }
            claimableFaction.getClaims().clear();
            claimableFaction.setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), player);
            return true;
        }
        claimableFaction.addClaim(new Claim(claimableFaction, selection.getMinimumPoint(), selection.getMaximumPoint()), player);
        sender.sendMessage(ChatColor.YELLOW + "Added claim for " + faction.getDisplayName(sender) + ChatColor.YELLOW + '.');
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        switch (args.length){
            case 2 :{
                final List<String> results = new ArrayList<>(plugin.getFactionManager().getClaimableFactions().size());
                for(ClaimableFaction claimableFaction : plugin.getFactionManager().getClaimableFactions()){
                    results.add(claimableFaction.getName());
                }
                return results;
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
    static {
        MIN_EVENT_CLAIM_AREA = 2;
    }

}