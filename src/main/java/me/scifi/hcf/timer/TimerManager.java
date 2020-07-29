package me.scifi.hcf.timer;

import com.doctordark.util.Config;
import lombok.Data;
import lombok.Getter;
import me.scifi.hcf.managers.IManager;
import me.scifi.hcf.timer.type.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import me.scifi.hcf.HCF;
import me.scifi.hcf.eventgame.EventTimer;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class TimerManager implements Listener, IManager {

    private CombatTimer combatTimer;

    private LogoutTimer logoutTimer;

    private EnderPearlTimer enderPearlTimer;

    private EventTimer eventTimer;

    private GappleTimer gappleTimer;

    private AppleTimer appleTimer;

    private PvPTimer pvpTimer;

    private PvpClassWarmupTimer pvpClassWarmupTimer;

    private StuckTimer stuckTimer;

    private TeleportTimer teleportTimer;

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
    public void load() {
        config = new Config(plugin, "timers",plugin.getDataFolder().getAbsolutePath());
        for (Timer timer : timers) {
            timer.load(config);
        }
    }

    /**
     * Saves the {@link Timer} data to storage.
     */
    public void unload() {
        for (Timer timer : timers) {
            timer.onDisable(config);
        }

        config.save();
    }
}
