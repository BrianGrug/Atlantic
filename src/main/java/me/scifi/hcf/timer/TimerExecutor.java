package me.scifi.hcf.timer;

import com.doctordark.util.command.ArgumentExecutor;
import me.scifi.hcf.timer.argument.TimerCheckArgument;
import me.scifi.hcf.timer.argument.TimerSetArgument;
import me.scifi.hcf.HCF;

/**
 * Handles the execution and tab completion of the timer command.
 */
public class TimerExecutor extends ArgumentExecutor {

    public TimerExecutor(HCF plugin) {
        super("timer");

        addArgument(new TimerCheckArgument(plugin));
        addArgument(new TimerSetArgument(plugin));
    }
}