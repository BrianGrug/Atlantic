package me.scifi.hcf.customtimers;

import com.doctordark.util.JavaUtils;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CustomTimerCommand implements CommandExecutor {

    private HCF plugin;

    public CustomTimerCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(command.getPermission())) {
            if (args.length < 1) {
                List<String> help = Utils.list(plugin.getMessagesYML().getStringList("CUSTOMTIMER-HELP"));
                help.forEach(sender::sendMessage);
                return true;
            }

            switch (args[0]) {
                case "list": {
                    List<String> names = new ArrayList<>();
                    plugin.getManagerHandler().getCustomTimerManager().getCustomTimers().forEach(timer -> names.add(timer.getName()));
                    if (names.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < names.size(); i++) {
                            sb.append(names.get(i)).append(",");
                        }
                        sender.sendMessage("6&lActive Timers:");
                        sender.sendMessage(Utils.chat("&e" + sb.toString()));
                        return true;
                    }
                    sender.sendMessage(Utils.chat("&cNo Custom Timers are active."));
                    break;
                }

                case "end": {
                    if (args.length < 2) {
                        sender.sendMessage(Utils.chat("&cUsage: /customtimer end <timer>"));
                        return true;
                    }
                    CustomTimer timer = plugin.getManagerHandler().getCustomTimerManager().getCustomTimer(args[1]);
                    if (timer == null) {
                        sender.sendMessage(Utils.chat("&cThis timer is not currently active."));
                        return true;
                    }
                    timer.cancel();
                    break;
                }

                case "start": {
                    if (args.length < 4) {
                        sender.sendMessage(Utils.chat("&cUsage: /customtimer start <name> <duration> <scoreboard>"));
                        return true;
                    }

                    long duration = JavaUtils.parse(args[2]);

                    if (duration == -1L) {
                        sender.sendMessage(Utils.chat("&c" + args[1] + " is an invalid duration."));
                        return true;
                    }

                    if (duration < 1000L) {
                        sender.sendMessage(Utils.chat("&cThe timer must last for at least 20 ticks."));
                        return true;
                    }

                    if (plugin.getManagerHandler().getCustomTimerManager().getCustomTimer(args[1]) != null) {
                        sender.sendMessage(Utils.chat("&cA timer with this name already exists."));
                        return true;
                    }

                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 3; i < args.length; i++) {
                        stringBuilder.append(args[i]).append(" ");
                    }

                    plugin.getManagerHandler().getCustomTimerManager().createTimer(new CustomTimer(Utils.chat(args[1]), Utils.chat(stringBuilder.toString().trim()), System.currentTimeMillis(), System.currentTimeMillis() + duration));
                    sender.sendMessage(Utils.chat("&cThe custom timer has been created."));
                    break;
                }
                default: {
                    List<String> help = Utils.list(plugin.getMessagesYML().getStringList("CUSTOMTIMER-HELP"));
                    help.forEach(sender::sendMessage);
                    break;
                }
            }
        }
        return false;
    }
}
