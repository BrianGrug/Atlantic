package me.scifi.hcf.command;

import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import me.scifi.hcf.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.timer.type.PvPTimer;

import java.util.Collections;
import java.util.List;

/**
 * Command used to manage the {@link PvPTimer} of {@link Player}s.
 */
public class PvpTimerCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public PvpTimerCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.chat(ChatColor.RED + "This command is only executable by players."));
            return true;
        }

        Player player = (Player) sender;
        PvPTimer pvpTimer = plugin.getManagerHandler().getTimerManager().getPvpTimer();

        if (args.length < 1) {
            printUsage(sender, label, pvpTimer);
            return true;
        }

        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
            if (pvpTimer.getRemaining(player) <= 0L) {
                sender.sendMessage(Utils.chat(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active."));
                return true;
            }

            sender.sendMessage(Utils.chat(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " timer is now off."));
            pvpTimer.clearCooldown(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("check")) {
            long remaining = pvpTimer.getRemaining(player);
            if (remaining <= 0L) {
                sender.sendMessage(Utils.chat(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active."));
                return true;
            }

            sender.sendMessage(Utils.chat(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD
                    + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.'));

            return true;
        }

        printUsage(sender, label, pvpTimer);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");

    /**
     * Prints the usage of this command to a sender.
     *
     * @param sender
     *            the sender to print for
     * @param label
     *            the label used for command
     */
    private void printUsage(CommandSender sender, String label, PvPTimer pvpTimer) {
        sender.sendMessage(ChatColor.GOLD + "*** " + pvpTimer.getName() + " Timer Help ***");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " enable - Removes your " + pvpTimer.getDisplayName() + ChatColor.GRAY + " timer.");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " time - Check remaining " + pvpTimer.getDisplayName() + ChatColor.GRAY + " time.");
        sender.sendMessage(ChatColor.GRAY + "/lives - Life and deathban related commands.");
    }
}
