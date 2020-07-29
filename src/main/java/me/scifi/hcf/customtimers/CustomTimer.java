package me.scifi.hcf.customtimers;

import lombok.Getter;
import lombok.Setter;
import me.scifi.hcf.HCF;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


@Getter @Setter
public class CustomTimer{

    private String name;

    private String scoreboard;

    public long startMillis;

    public long endMillis;

    public long getRemaining;

    public BukkitTask task;

    public CustomTimer(String name, String scoreboard, long startMillis, long endMillis) {
        setName(name);
        setScoreboard(scoreboard);
        setStartMillis(startMillis);
        setEndMillis(endMillis);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (endMillis < System.currentTimeMillis()) {
                    HCF.getPlugin().getManagerHandler().getCustomTimerManager().deleteTimer(HCF.getPlugin().getManagerHandler().getCustomTimerManager().getCustomTimer(name));
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(HCF.getPlugin(), 0, 20);
    }

    public long getRemaining(){
       return endMillis - System.currentTimeMillis();
    }

    public void cancel() {
        HCF.getPlugin().getManagerHandler().getCustomTimerManager().deleteTimer(this);
    }
}
