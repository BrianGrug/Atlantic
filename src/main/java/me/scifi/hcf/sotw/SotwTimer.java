package me.scifi.hcf.sotw;

import lombok.Getter;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import me.scifi.hcf.HCF;

import java.util.List;

public class SotwTimer {

    @Getter
    private SotwRunnable sotwRunnable;

    public boolean cancel() {
        if (this.sotwRunnable != null) {
            this.sotwRunnable.cancel();
            this.sotwRunnable = null;
            SotwCommand.enabled.clear();
            return true;
        }

        return false;
    }

    public void start(long millis) {
        if (this.sotwRunnable == null) {
            this.sotwRunnable = new SotwRunnable(this, millis);
            this.sotwRunnable.runTaskLater(HCF.getPlugin(), millis / 50L);
        }
    }

    public static class SotwRunnable extends BukkitRunnable {

        private SotwTimer sotwTimer;
        private long startMillis;
        private long endMillis;

        public SotwRunnable(SotwTimer sotwTimer, long duration) {
            this.sotwTimer = sotwTimer;
            this.startMillis = System.currentTimeMillis();
            this.endMillis = this.startMillis + duration;
        }

        public long getRemaining() {
            return endMillis - System.currentTimeMillis();
        }

        @Override
        public void run() {
            List<String> sotwEnded = HCF.getPlugin().messagesYML.getStringList("SOTW-ENDED-MESSAGE");
            sotwEnded.forEach(str -> Bukkit.broadcastMessage(Utils.chat(str)));
            this.cancel();
            this.sotwTimer.sotwRunnable = null;
        }
    }
}
