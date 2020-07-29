package me.scifi.hcf.faction.argument.subclaim;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.claim.Claim;
import me.scifi.hcf.faction.claim.ClaimHandler;
import me.scifi.hcf.faction.claim.ClaimSelection;
import me.scifi.hcf.faction.claim.Subclaim;
import me.scifi.hcf.faction.struct.Role;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FactionSubclaimCreateArgument extends CommandArgument {

    private final HCF plugin;

    public FactionSubclaimCreateArgument(HCF plugin) {
        super("create", "Create a subclaim with a selection", new String[] { "make", "build" });
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + " subclaim " + getName() + " <subclaimName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getManagerHandler().getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        UUID uuid = player.getUniqueId();

        if (playerFaction.getMember(uuid).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to create subclaims.");
            return true;
        }

        if (args[2].length() < ConfigurationService.SUBCLAIM_NAME_CHARACTERS_MIN) {
            sender.sendMessage(ChatColor.RED + "Subclaim names must have at least " + ConfigurationService.SUBCLAIM_NAME_CHARACTERS_MIN + " characters.");
            return true;
        }

        if (args[2].length() > ConfigurationService.SUBCLAIM_NAME_CHARACTERS_MAX) {
            sender.sendMessage(ChatColor.RED + "Subclaim names cannot be longer than " + ConfigurationService.SUBCLAIM_NAME_CHARACTERS_MAX + " characters.");
            return true;
        }

        if (!JavaUtils.isAlphanumeric(args[2])) {
            sender.sendMessage(ChatColor.RED + "Subclaim names may only be alphanumeric.");
            return true;
        }

        for (Claim claim : playerFaction.getClaims()) {
            if (claim.getSubclaim(args[2]) != null) {
                sender.sendMessage(ChatColor.RED + "Your faction already has a subclaim named " + args[2] + '.');
                return true;
            }
        }

        Map<UUID, ClaimSelection> selectionMap = plugin.getManagerHandler().getClaimHandler().claimSelectionMap;
        ClaimSelection claimSelection = selectionMap.get(uuid);

        if (claimSelection == null || !claimSelection.hasBothPositionsSet()) {
            sender.sendMessage(ChatColor.RED + "You have not set both positions of this subclaim.");
            return true;
        }

        Subclaim subclaim = new Subclaim(playerFaction, claimSelection.getPos1(), claimSelection.getPos2());
        subclaim.setY1(ClaimHandler.MIN_CLAIM_HEIGHT);
        subclaim.setY2(ClaimHandler.MAX_CLAIM_HEIGHT);
        subclaim.setName(args[2]);
        if (plugin.getManagerHandler().getClaimHandler().tryCreatingSubclaim(player, subclaim)) {
            plugin.getManagerHandler().getVisualiseHandler().clearVisualBlock(player, subclaim.getMinimumPoint());
            plugin.getManagerHandler().getVisualiseHandler().clearVisualBlock(player, subclaim.getMaximumPoint());
            selectionMap.remove(uuid);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
