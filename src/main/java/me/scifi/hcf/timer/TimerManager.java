package me.scifi.hcf.timer;

import com.doctordark.util.Config;
import lombok.Data;
import lombok.Getter;
import me.scifi.hcf.timer.type.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import me.scifi.hcf.HCF;
import me.scifi.hcf.eventgame.EventTimer;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class TimerManager implements Listener {

    @Getter
    private CombatTimer combatTimer;

    @Getter
    private LogoutTimer logoutTimer;

    @Getter
    private EnderPearlTimer enderPearlTimer;

    @Getter
    private EventTimer eventTimer;

    @Getter
    private GappleTimer gappleTimer;

    @Getter
    private AppleTimer appleTimer;

    @Getter
    private PvPTimer pvpTimer;

    @Getter
    private PvpClassWarmupTimer pvpClassWarmupTimer;

    @Getter
    private StuckTimer stuckTimer;

    @Getter
    private TeleportTimer teleportTimer;

    @Getter
    private Set<Timer> timers = new LinkedHashSet<>();

    private JavaPlugin plugin;
    private Config config;

    public TimerManager(HCF plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
        if(plugin.getConfig().getBoolean("core-enderpearls")) {
            registerTimer(enderPearlTimer = new EnderPearlTimer(plugin));
        }
        registerTimer(logoutTimer = new LogoutTimer());
        registerTimer(gappleTimer = new GappleTimer(plugin));
        registerTimer(stuckTimer = new StuckTimer());
        registerTimer(pvpTimer = new PvPTimer(plugin));
        registerTimer(combatTimer = new CombatTimer(plugin));
        registerTimer(teleportTimer = new TeleportTimer(plugin));
        registerTimer(eventTimer = new EventTimer(plugin));
        registerTimer(pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin));
        registerTimer(appleTimer = new AppleTimer(plugin));
        reloadTimerData();
    }

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }

    /**
     * Reloads the {@link Timer} data from storage.
     */
    public void reloadTimerData() {
        config = new Config(plugin, "timers",plugin.getDataFolder().getAbsolutePath());
        for (Timer timer : timers) {
            timer.load(config);
        }
    }

    /**
     * Saves the {@link Timer} data to storage.
     */
    public void saveTimerData() {
        for (Timer timer : timers) {
            timer.onDisable(config);
        }

        config.save();
    }
}
