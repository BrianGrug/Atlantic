package me.scifi.hcf.deathban.lives;

import com.doctordark.util.command.ArgumentExecutor;
import me.scifi.hcf.HCF;
import me.scifi.hcf.deathban.lives.argument.LivesCheckArgument;
import me.scifi.hcf.deathban.lives.argument.LivesCheckDeathbanArgument;
import me.scifi.hcf.deathban.lives.argument.LivesClearDeathbansArgument;
import me.scifi.hcf.deathban.lives.argument.LivesGiveArgument;
import me.scifi.hcf.deathban.lives.argument.LivesReviveArgument;
import me.scifi.hcf.deathban.lives.argument.LivesSetArgument;
import me.scifi.hcf.deathban.lives.argument.LivesSetDeathbanTimeArgument;
import me.scifi.hcf.deathban.lives.argument.LivesTopArgument;

/**
 * Handles the execution and tab completion of the lives command.
 */
public class LivesExecutor extends ArgumentExecutor {

    public LivesExecutor(HCF plugin) {
        super("lives");

        addArgument(new LivesCheckArgument(plugin));
        addArgument(new LivesCheckDeathbanArgument(plugin));
        addArgument(new LivesClearDeathbansArgument(plugin));
        addArgument(new LivesGiveArgument(plugin));
        addArgument(new LivesReviveArgument(plugin));
        addArgument(new LivesSetArgument(plugin));
        addArgument(new LivesSetDeathbanTimeArgument());
        addArgument(new LivesTopArgument(plugin));
    }
}