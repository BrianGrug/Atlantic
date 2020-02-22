package me.scifi.hcf.eventgame;

import com.doctordark.util.command.ArgumentExecutor;
import me.scifi.hcf.HCF;
import me.scifi.hcf.eventgame.argument.EventCancelArgument;
import me.scifi.hcf.eventgame.argument.EventCreateArgument;
import me.scifi.hcf.eventgame.argument.EventDeleteArgument;
import me.scifi.hcf.eventgame.argument.EventRenameArgument;
import me.scifi.hcf.eventgame.argument.EventSetAreaArgument;
import me.scifi.hcf.eventgame.argument.EventSetCapzoneArgument;
import me.scifi.hcf.eventgame.argument.EventStartArgument;
import me.scifi.hcf.eventgame.argument.EventUptimeArgument;

/**
 * Handles the execution and tab completion of the event command.
 */
public class EventExecutor extends ArgumentExecutor {

    public EventExecutor(HCF plugin) {
        super("event");

        addArgument(new EventCancelArgument(plugin));
        addArgument(new EventCreateArgument(plugin));
        addArgument(new EventDeleteArgument(plugin));
        addArgument(new EventRenameArgument(plugin));
        addArgument(new EventSetAreaArgument(plugin));
        addArgument(new EventSetCapzoneArgument(plugin));
        addArgument(new EventStartArgument(plugin));
        addArgument(new EventUptimeArgument(plugin));
    }
}