package me.scifi.hcf.scoreboard;

import com.google.common.collect.Ordering;
import me.scifi.hcf.DateTimeFormats;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.customtimers.CustomTimer;
import me.scifi.hcf.eventgame.EventTimer;
import me.scifi.hcf.eventgame.eotw.EotwHandler;
import me.scifi.hcf.eventgame.faction.ConquestFaction;
import me.scifi.hcf.eventgame.faction.EventFaction;
import me.scifi.hcf.eventgame.faction.KothFaction;
import me.scifi.hcf.eventgame.tracker.ConquestTracker;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.pvpclass.PvpClass;
import me.scifi.hcf.pvpclass.archer.ArcherClass;
import me.scifi.hcf.pvpclass.archer.ArcherMark;
import me.scifi.hcf.pvpclass.bard.BardClass;
import me.scifi.hcf.sotw.SotwCommand;
import me.scifi.hcf.sotw.SotwTimer;
import me.scifi.hcf.staffmode.StaffModeCommand;
import me.scifi.hcf.staffmode.Vanish;
import me.scifi.hcf.timer.PlayerTimer;
import me.scifi.hcf.timer.Timer;
import me.scifi.hcf.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Adapter implements AssembleAdapter {

    public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("00.0");
        }
    };
    private static final Comparator<Map.Entry<UUID, ArcherMark>> ARCHER_MARK_COMPARATOR = (o1, o2) -> o1.getValue().compareTo(o2.getValue());

    private static String handleBardFormat(long millis, boolean trailingZero) {
        return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.TITLE"));
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        UserManager um = HCF.getPlugin().getManagerHandler().getUserManager();

        if (HCF.getPlugin().getConfig().getBoolean("kit-map")) {
            if (HCF.getPlugin().getConfig().getBoolean("kit-map-stats-in-spawn-only")) {
                if (HCF.getPlugin().getManagerHandler().getFactionManager().getClaimAt(player.getLocation()).getFaction().isSafezone()) {
                    lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.KILLS.TITLE")
                            .replace("%kills%", Integer.toString(player.getStatistic(Statistic.PLAYER_KILLS))));
                    lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.DEATHS.TITLE")
                            .replace("%deaths%", Integer.toString(player.getStatistic(Statistic.DEATHS))));
                    if (HCF.getPlugin().getMessagesYML().getBoolean("SCOREBOARD.BALANCE.ENABLED")) {
                        lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.BALANCE.TITLE")
                                .replace("%balance%", Integer.toString(HCF.getPlugin().getManagerHandler().getEconomyManager().getBalance(player.getUniqueId()))));
                    }
                }
            } else {
                lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.KILLS.TITLE")
                        .replace("%kills%", Integer.toString(um.getUserAsync(player.getUniqueId()).getKills())));
                lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.DEATHS.TITLE")
                        .replace("%deaths%", Integer.toString(um.getUserAsync(player.getUniqueId()).getDeaths())));
                if (HCF.getPlugin().getMessagesYML().getBoolean("SCOREBOARD.BALANCE.ENABLED")) {
                    lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.BALANCE.TITLE")
                            .replace("%balance%", Integer.toString(HCF.getPlugin().getManagerHandler().getEconomyManager().getBalance(player.getUniqueId()))));
                }
            }
        }



        EotwHandler.EotwRunnable eotwRunnable = HCF.getPlugin().getManagerHandler().getEotwHandler().getRunnable();
        if (eotwRunnable != null) {
            long remaining = eotwRunnable.getMillisUntilStarting();
            if (remaining > 0L) {
                lines.add(ChatColor.RED.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED + " starts" + " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true));
            } else if ((remaining = eotwRunnable.getMillisUntilCappable()) > 0L) {
                lines.add(ChatColor.RED.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED + " cappable" + " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true));
            }
        }

        SotwTimer.SotwRunnable sotwRunnable = HCF.getPlugin().getSotwTimer().getSotwRunnable();
        if (sotwRunnable != null) {
            if (SotwCommand.enabled.contains(player.getUniqueId())) {
                lines.add(Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.SOTW.ENABLED")
                        .replace("%remain%", DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true))));
            } else {
                lines.add(Utils.chat(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.SOTW.REGULAR")
                        .replace("%remaining%", DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true))));
            }
        }


        if (HCF.getPlugin().getManagerHandler().getKingManager().isEventActive()) {
            int x = HCF.getPlugin().getManagerHandler().getKingManager().getKingPlayer().getLocation().getBlockX();
            int z = HCF.getPlugin().getManagerHandler().getKingManager().getKingPlayer().getLocation().getBlockZ();
            lines.add("&3&lKing Event&7:");
            lines.add(" &7\u00BB &3King Name&7: &f" + HCF.getPlugin().getManagerHandler().getKingManager().getKingPlayer().getName());
            lines.add(" &7\u00BB &3Coords&7: &f" + x + " , " + z);
        }

        if (StaffModeCommand.staffMode.contains(player.getUniqueId())) {
            DecimalFormat decimalFormat = new DecimalFormat("##.00");
            lines.add("&3&lStaff Mode&7:");
            if (Vanish.isPlayerVanished(player)) {
                lines.add("&7» &3Vanished&7: &fEnabled");
            } else {
                lines.add("&7» &3Vanished&7: &fDisabled");
            }
            lines.add("&7» &3Online&7: &f" + HCF.getOnlinePlayers().size());
            double tps = Bukkit.spigot().getTPS()[0];
            if(tps > 20) lines.add("&7» &3TPS: &f" + 20.00);

            else {
                lines.add("&7» &3TPS: &f" + decimalFormat.format(Bukkit.spigot().getTPS()[0]));
            }
        }

        EventTimer eventTimer = HCF.getPlugin().getManagerHandler().getTimerManager().getEventTimer();
        List<String> conquestLines = null;

        EventFaction eventFaction = eventTimer.getEventFaction();
        if (eventFaction instanceof KothFaction) {
            lines.add(eventFaction.getScoreboardName()
                    .replace("%remaining%", DurationFormatter.getRemaining(eventTimer.getRemaining(), true)));
        } else if (eventFaction instanceof ConquestFaction) {
            ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            DecimalFormat format = CONQUEST_FORMATTER.get();

            conquestLines = new ArrayList<>();
            conquestLines.add(eventFaction.getScoreboardName());
            conquestLines.add("&c&lRed&7: &f" + conquestFaction.getRed().getScoreboardRemaining());
            conquestLines.add("&e&lYellow&7: &f" + conquestFaction.getYellow().getScoreboardRemaining());
            conquestLines.add("&9&lBlue&7: &f" + conquestFaction.getBlue().getScoreboardRemaining());
            conquestLines.add("&2&lGreen&7: &f" + conquestFaction.getGreen().getScoreboardRemaining());


            // Show the top 3 factions next.
            ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
            int count = 0;
            for (Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap().entrySet()) {
                String factionName = entry.getKey().getName();
                if (factionName.length() > 14) factionName = factionName.substring(0, 14);
                conquestLines.add(ChatColor.GOLD.toString() + "\u00bb " + ChatColor.LIGHT_PURPLE + factionName + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());
                if (++count == 3) break;
            }
        }

        // Show the current PVP Class statistics of the player.
        PvpClass pvpClass = HCF.getPlugin().getManagerHandler().getPvpClassManager().getEquippedClass(player);
        if (pvpClass != null) {
            lines.add(ChatColor.AQUA.toString() + ChatColor.BOLD + "Active Class" + ChatColor.GRAY.toString() + ": " + ChatColor.WHITE + pvpClass.getName());
            if (pvpClass instanceof BardClass) {
                BardClass bardClass = (BardClass) pvpClass;
                lines.add(ChatColor.GOLD + " \u00bb " + ChatColor.LIGHT_PURPLE + "Energy" + ChatColor.GRAY + ": " + ChatColor.WHITE +
                        handleBardFormat(bardClass.getEnergyMillis(player), true));

                long remaining = bardClass.getRemainingBuffDelay(player);
                if (remaining > 0) {
                    lines.add(ChatColor.GOLD.toString() + " \u00bb " + ChatColor.LIGHT_PURPLE + "Buff Delay" +
                            ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(remaining, true));
                }
            } else if (pvpClass instanceof ArcherClass) {
                ArcherClass archerClass = (ArcherClass) pvpClass;

                List<Map.Entry<UUID, ArcherMark>> entryList = Ordering.from(ARCHER_MARK_COMPARATOR).sortedCopy(archerClass.getSentMarks(player).entrySet());
                entryList = entryList.subList(0, Math.min(entryList.size(), 3));
                for (Map.Entry<UUID, ArcherMark> entry : entryList) {
                    ArcherMark archerMark = entry.getValue();
                    Player target = Bukkit.getPlayer(entry.getKey());
                    if (target != null) {
                        ChatColor levelColour;
                        switch (archerMark.currentLevel) {
                            case 1:
                                levelColour = ChatColor.GREEN;
                                break;
                            case 2:
                                levelColour = ChatColor.RED;
                                break;
                            case 3:
                                levelColour = ChatColor.DARK_RED;
                                break;

                            default:
                                levelColour = ChatColor.YELLOW;
                                break;
                        }

                        // Add the current mark level to scoreboard.
                        //lines.add(new SidebarEntry(ChatColor.GOLD + "" + ChatColor.BOLD, "Archer Mark" + ChatColor.GRAY + ": ", ""));
                        String targetName = target.getName();
                        targetName = targetName.substring(0, Math.min(targetName.length(), 15));
                        lines.add(ChatColor.GOLD + " \u00bb" + ChatColor.RED + ' ' + targetName + ChatColor.YELLOW.toString() + levelColour + " [Mark " + archerMark.currentLevel + ']');

                    }
                }
            }
        }

        Collection<Timer> timers = HCF.getPlugin().getManagerHandler().getTimerManager().getTimers();
        for (Timer timer : timers) {
            if (timer instanceof PlayerTimer) {
                PlayerTimer playerTimer = (PlayerTimer) timer;
                long remaining = playerTimer.getRemaining(player);
                if (remaining <= 0) continue;

                String timerName = playerTimer.getName();
                if (timerName.length() > 14) timerName = timerName.substring(0, timerName.length());
                lines.add(playerTimer.getScoreboardPrefix() + timerName + ChatColor.GRAY + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(remaining, true));
            }
        }

        Collection<CustomTimer> customTimers = HCF.getPlugin().getManagerHandler().getCustomTimerManager().getCustomTimers();
        for (CustomTimer timer : customTimers) {

            lines.add(timer.getScoreboard() + "&7: &f" + DurationFormatter.getRemaining(timer.getRemaining(), true));
        }

        if (conquestLines != null && !conquestLines.isEmpty()) {
            if (!lines.isEmpty()) {
                conquestLines.add("" + "" + "");
            }

            conquestLines.addAll(lines);
            lines = conquestLines;
        }


        if (!lines.isEmpty()) {
            lines.add(0, HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.SEPARATOR"));
            if (HCF.getPlugin().getConfig().getBoolean("SCOREBOARD-FOOTER")) {
                lines.add(" ");
                lines.add(HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.FOOTER"));
            }
            lines.add(lines.size(), HCF.getPlugin().getMessagesYML().getString("SCOREBOARD.SEPARATOR"));
        }

        lines.forEach(string -> ChatColor.translateAlternateColorCodes('&', string));
        return lines;
    }
}

