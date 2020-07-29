package me.scifi.hcf.eventgame.eotw;

import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.type.ClaimableFaction;
import me.scifi.hcf.faction.type.Faction;
import me.scifi.hcf.managers.IManager;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import me.scifi.hcf.listener.BorderListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Handles the EndOfTheWorld mini-game which shrinks the border and runs a KOTH event.
 */
public class EotwHandler implements IManager {

    public static final int BORDER_DECREASE_MINIMUM = 500;
    public static final int BORDER_DECREASE_AMOUNT = 200;

    public static final long BORDER_DECREASE_TIME_MILLIS = TimeUnit.SECONDS.toMillis(20L);
    public static final int BORDER_DECREASE_TIME_SECONDS = (int) TimeUnit.MILLISECONDS.toSeconds(BORDER_DECREASE_TIME_MILLIS);
    public static final int BORDER_DECREASE_TIME_SECONDS_HALVED = BORDER_DECREASE_TIME_SECONDS / 2;
    public static final String BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS, true, true);
    public static final String BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS / 2, true, true);

    public static final long EOTW_WARMUP_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    public static final int EOTW_WARMUP_WAIT_SECONDS = (int) (TimeUnit.MILLISECONDS.toSeconds(EOTW_WARMUP_WAIT_MILLIS));

    private static final long EOTW_CAPPABLE_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(1L);
    private static final int WITHER_INTERVAL_SECONDS = 10;

    private EotwRunnable runnable;
    private final HCF plugin;

    public EotwHandler(HCF plugin) {
        this.plugin = plugin;
    }

    public EotwRunnable getRunnable() {
        return runnable;
    }

    /**
     * Checks if the map is currently in 'End of the World' mode.
     *
     * @return true if the map is the end of world
     */
    public boolean isEndOfTheWorld() {
        return isEndOfTheWorld(true);
    }

    /**
     * Checks if the map is currently in 'End of the World' mode.
     *
     * @param ignoreWarmup
     *            if the warmup stage is ignored
     * @return true if the map is the end of world
     */
    public boolean isEndOfTheWorld(boolean ignoreWarmup) {
        return runnable != null && (!ignoreWarmup || runnable.getElapsedMilliseconds() > 0);
    }

    /**
     * Sets if the server is currently in 'End of the World' mode.
     *
     * @param yes
     *            the value to set
     */
    public void setEndOfTheWorld(boolean yes) {
        // Don't unnecessary edit task.
        if (yes == isEndOfTheWorld(false)) {
            return;
        }

        if (yes) {
            runnable = new EotwRunnable();
            runnable.runTaskTimer(plugin, 20L, 20L);
        } else {
            if (runnable != null) {
                runnable.cancel();
                runnable = null;
            }
        }
    }

    public static final class EotwRunnable extends BukkitRunnable {

        private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 200, 0);

        // The set of players that should be given the Wither potion effect because they are outside of the border.
        private final Set<Player> outsideBorder = new HashSet<>();

        private long startStamp;
        private int elapsedSeconds;

        public EotwRunnable() {
            this.startStamp = System.currentTimeMillis() + EOTW_WARMUP_WAIT_MILLIS;
            this.elapsedSeconds = -EOTW_WARMUP_WAIT_SECONDS;
        }

        public void handleDisconnect(Player player) {
            outsideBorder.remove(player);
        }

        public long getMillisUntilStarting() {
            long difference = System.currentTimeMillis() - startStamp;
            return difference > 0L ? -1L : Math.abs(difference);
        }

        public long getMillisUntilCappable() {
            return EOTW_CAPPABLE_WAIT_MILLIS - getElapsedMilliseconds();
        }

        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - startStamp;
        }

        @Override
        public void run() {
            elapsedSeconds++;

            if (elapsedSeconds == 0) {
                for (Faction faction : HCF.getPlugin().getManagerHandler().getFactionManager().getFactions()) {
                    if (faction instanceof ClaimableFaction) {
                        ClaimableFaction claimableFaction = (ClaimableFaction) faction;
                        claimableFaction.removeClaims(claimableFaction.getClaims(), Bukkit.getConsoleSender());
                    }
                }

                Bukkit.broadcastMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "EndOfTheWorld" + ChatColor.DARK_AQUA + " has began. Border will decrease by " + BORDER_DECREASE_AMOUNT
                        + " blocks every " + BORDER_DECREASE_TIME_WORDS + " until at " + BORDER_DECREASE_MINIMUM + " blocks.");

                return;
            }

            if (elapsedSeconds < 0 && elapsedSeconds >= -EOTW_WARMUP_WAIT_SECONDS) {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "EndOfTheWorld" + ChatColor.DARK_AQUA + " is starting in "
                        + DurationFormatter.getRemaining(Math.abs(elapsedSeconds) * 1000L, true, false) + '.');

                return;
            }

            // Wither those outside of the border every 10 seconds.
            if (elapsedSeconds % WITHER_INTERVAL_SECONDS == 0) {
                Iterator<Player> iterator = outsideBorder.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();

                    if (BorderListener.isWithinBorder(player.getLocation())) {
                        iterator.remove();
                        continue;
                    }

                    player.sendMessage(ChatColor.RED + "You are currently outside of the border during EOTW, so you were withered.");
                    player.addPotionEffect(WITHER, true);
                }
            }

            for (World.Environment current : World.Environment.values()) {
                int borderSize = ConfigurationService.BORDER_SIZES.get(current);
                int newBorderSize = borderSize - BORDER_DECREASE_AMOUNT;
                if (newBorderSize <= BORDER_DECREASE_MINIMUM) {
                    ConfigurationService.BORDER_SIZES.put(current, BORDER_DECREASE_MINIMUM);
                    continue;
                }
                if (elapsedSeconds % BORDER_DECREASE_TIME_SECONDS == 0) {
                    ConfigurationService.BORDER_SIZES.put(current, borderSize = newBorderSize);
                    String msg = (ChatColor.DARK_AQUA + "Border has been decreased to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks.");

                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (player.getWorld().getEnvironment().equals(current))
                            player.sendMessage(msg);
                    }

                    // Update list of players outside of the border now it has shrunk.
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {

                        if (!BorderListener.isWithinBorder(player.getLocation())) {
                            outsideBorder.add(player);
                        }

                    }
                } else if (elapsedSeconds % BORDER_DECREASE_TIME_SECONDS_HALVED == 0) {
                    String msg2 = (ChatColor.DARK_AQUA + "Border decreasing to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks in " + ChatColor.YELLOW
                            + BORDER_DECREASE_TIME_ALERT_WORDS + ChatColor.DARK_AQUA + '.');
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (player.getWorld().getEnvironment().equals(current))
                            player.sendMessage(msg2);
                    }
                }
            }
        }
    }
}
