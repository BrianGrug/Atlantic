package com.doctordark.util.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.doctordark.util.*;
import org.bukkit.*;
import org.apache.commons.lang3.text.*;
import java.util.*;
import java.io.*;

public class CommandWrapper implements CommandExecutor, TabCompleter {
    private final Collection<CommandArgument> arguments;

    public CommandWrapper(final Collection<CommandArgument> arguments) {
        this.arguments = arguments;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            printUsage(sender, label, this.arguments);
            return true;
        }
        final CommandArgument argument = matchArgument(args[0], sender, this.arguments);
        if (argument == null) {
            printUsage(sender, label, this.arguments);
            return true;
        }
        try {
            return argument.onCommand(sender, command, label, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        List<String> results;
        if (args.length == 1) {
            results = getAccessibleArgumentNames(sender, this.arguments);
        } else {
            final CommandArgument argument = matchArgument(args[0], sender, this.arguments);
            if (argument == null) {
                return Collections.emptyList();
            }
            results = argument.onTabComplete(sender, command, label, args);
            if (results == null) {
                return null;
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }

    public static void printUsage(final CommandSender sender, final String label, final Collection<CommandArgument> arguments) {
        sender.sendMessage(ChatColor.DARK_AQUA + "*** " + WordUtils.capitalizeFully(label) + " Help ***");
        for (final CommandArgument argument : arguments) {
            final String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription());
            }
        }
    }

    public static CommandArgument matchArgument(final String id, final CommandSender sender, final Collection<CommandArgument> arguments) {
        for (final CommandArgument argument : arguments) {
            final String permission = argument.getPermission();
            if ((permission == null || sender.hasPermission(permission)) && (argument.getName().equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id))) {
                return argument;
            }
        }
        return null;
    }

    public static List<String> getAccessibleArgumentNames(final CommandSender sender, final Collection<CommandArgument> arguments) {
        final List<String> results = new ArrayList<String>();
        for (final CommandArgument argument : arguments) {
            final String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                results.add(argument.getName());
            }
        }
        return results;
    }

    public static class ArgumentComparator implements Comparator<CommandArgument>, Serializable {
        @Override
        public int compare(final CommandArgument primaryArgument, final CommandArgument secondaryArgument) {
            return secondaryArgument.getName().compareTo(primaryArgument.getName());
        }
    }
}
