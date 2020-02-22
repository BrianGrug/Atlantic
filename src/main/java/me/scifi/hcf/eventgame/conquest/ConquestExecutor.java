package me.scifi.hcf.eventgame.conquest;

import com.doctordark.util.command.ArgumentExecutor;
import me.scifi.hcf.HCF;

public class ConquestExecutor extends ArgumentExecutor {

    public ConquestExecutor(HCF plugin) {
        super("conquest");
        addArgument(new ConquestSetpointsArgument(plugin));
    }
}
