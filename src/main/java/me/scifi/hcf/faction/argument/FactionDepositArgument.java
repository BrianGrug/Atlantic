package me.scifi.hcf.faction.argument;

import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

import me.scifi.hcf.economy.EconomyManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.struct.Relation;
import me.scifi.hcf.faction.type.PlayerFaction;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionDepositArgument extends CommandArgument {

    private final HCF plugin;

    public FactionDepositArgument(HCF plugin) {
        super("deposit", "Deposits money to the faction balance.", new String[]{"d"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <all|amount>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int playerBalance;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (args.length < 2) {
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
        playerBalance = plugin.getManagerHandler().getEconomyManager().getBalance(uuid);

        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = playerBalance;
        } else {
            if ((amount = (JavaUtils.tryParseInt(args[1]))) == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }

        if (playerBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You need at least " + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + " to do this, you only have " + EconomyManager.ECONOMY_SYMBOL
                    + JavaUtils.format(playerBalance) + '.');

            return true;
        }

        plugin.getManagerHandler().getEconomyManager().subtractBalance(uuid, amount);

        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.BOLD
                + EconomyManager.ECONOMY_SYMBOL + JavaUtils.format(amount) + ChatColor.YELLOW + " into the faction balance.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? COMPLETIONS : Collections.<String>emptyList();
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
}
