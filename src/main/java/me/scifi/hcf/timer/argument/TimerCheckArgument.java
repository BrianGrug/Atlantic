package me.scifi.hcf.timer.argument;

import com.doctordark.util.command.CommandArgument;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import me.scifi.hcf.HCF;
import me.scifi.hcf.UUIDFetcher;
import me.scifi.hcf.timer.PlayerTimer;
import me.scifi.hcf.timer.Timer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TimerCheckArgument extends CommandArgument {

    private final HCF plugin;

    public TimerCheckArgument(HCF plugin) {
        super("check", "Check remaining timer time");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <timerName> <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        PlayerTimer temporaryTimer = null;
        for (Timer timer : plugin.getManagerHandler().getTimerManager().getTimers()) {
            if (timer instanceof PlayerTimer && timer.getName().equalsIgnoreCase(args[1])) {
                temporaryTimer = (PlayerTimer) timer;
                break;
            }
        }

        if (temporaryTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }

        final PlayerTimer playerTimer = temporaryTimer;
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID uuid;
                try {
                    uuid = UUIDFetcher.getUUIDOf(args[2]);
                } catch (Exception ex) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
                    return;
                }

                long remaining = playerTimer.getRemaining(uuid);
                sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.emptyList();
    }
}
