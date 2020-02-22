package com.doctordark.util;

import java.lang.ref.*;
import org.bukkit.entity.*;
import com.google.common.base.*;
import java.util.*;

public class ExperienceManager {
    private static int hardMaxLevel;
    private static int[] xpTotalToReachLevel;
    private final WeakReference<Player> player;
    private final String playerName;

    public ExperienceManager(final Player player) {
        Preconditions.checkNotNull((Object) player, (Object) "Player cannot be null");
        this.player = new WeakReference<Player>(player);
        this.playerName = player.getName();
    }

    public static int getHardMaxLevel() {
        return ExperienceManager.hardMaxLevel;
    }

    public static void setHardMaxLevel(final int hardMaxLevel) {
        ExperienceManager.hardMaxLevel = hardMaxLevel;
    }

    private static void initLookupTables(final int maxLevel) {
        ExperienceManager.xpTotalToReachLevel = new int[maxLevel];
        for (int i = 0; i < ExperienceManager.xpTotalToReachLevel.length; ++i) {
            ExperienceManager.xpTotalToReachLevel[i] = ((i >= 30) ? ((int) (3.5 * i * i - 151.5 * i + 2220.0)) : ((i >= 16) ? ((int) (1.5 * i * i - 29.5 * i + 360.0)) : (17 * i)));
        }
    }

    private static int calculateLevelForExp(final int exp) {
        int level = 0;
        for (int curExp = 7, incr = 10; curExp <= exp; curExp += incr, ++level, incr += ((level % 2 == 0) ? 3 : 4)) {
        }
        return level;
    }

    public Player getPlayer() {
        final Player p = this.player.get();
        if (p == null) {
            throw new IllegalStateException("Player " + this.playerName + " is not online");
        }
        return p;
    }

    public void changeExp(final int amt) {
        this.changeExp((double) amt);
    }

    public void changeExp(final double amt) {
        this.setExp(this.getCurrentFractionalXP(), amt);
    }

    public void setExp(final int amt) {
        this.setExp(0.0, amt);
    }

    public void setExp(final double amt) {
        this.setExp(0.0, amt);
    }

    private void setExp(final double base, final double amt) {
        final int xp = (int) Math.max(base + amt, 0.0);
        final Player player = this.getPlayer();
        final int curLvl = player.getLevel();
        final int newLvl = this.getLevelForExp(xp);
        if (curLvl != newLvl) {
            player.setLevel(newLvl);
        }
        if (xp > base) {
            player.setTotalExperience(player.getTotalExperience() + xp - (int) base);
        }
        final double pct = (base - this.getXpForLevel(newLvl) + amt) / this.getXpNeededToLevelUp(newLvl);
        player.setExp((float) pct);
    }

    public int getCurrentExp() {
        final Player player = this.getPlayer();
        final int lvl = player.getLevel();
        return this.getXpForLevel(lvl) + Math.round(this.getXpNeededToLevelUp(lvl) * player.getExp());
    }

    private double getCurrentFractionalXP() {
        final Player player = this.getPlayer();
        final int lvl = player.getLevel();
        return this.getXpForLevel(lvl) + this.getXpNeededToLevelUp(lvl) * player.getExp();
    }

    public boolean hasExp(final int amt) {
        return this.getCurrentExp() >= amt;
    }

    public boolean hasExp(final double amt) {
        return this.getCurrentFractionalXP() >= amt;
    }

    public int getLevelForExp(final int exp) {
        if (exp <= 0) {
            return 0;
        }
        if (exp > ExperienceManager.xpTotalToReachLevel[ExperienceManager.xpTotalToReachLevel.length - 1]) {
            final int newMax = calculateLevelForExp(exp) * 2;
            Preconditions.checkArgument(newMax <= ExperienceManager.hardMaxLevel, (Object) ("Level for exp " + exp + " > hard max level " + ExperienceManager.hardMaxLevel));
            initLookupTables(newMax);
        }
        final int pos = Arrays.binarySearch(ExperienceManager.xpTotalToReachLevel, exp);
        return (pos < 0) ? (-pos - 2) : pos;
    }

    public int getXpNeededToLevelUp(final int level) {
        Preconditions.checkArgument(level >= 0, (Object) "Level may not be negative.");
        return (level > 30) ? (62 + (level - 30) * 7) : ((level >= 16) ? (17 + (level - 15) * 3) : 17);
    }

    public int getXpForLevel(final int level) {
        Preconditions.checkArgument(level >= 0 && level <= ExperienceManager.hardMaxLevel, (Object) ("Invalid level " + level + "(must be in range 0.." + ExperienceManager.hardMaxLevel + ')'));
        if (level >= ExperienceManager.xpTotalToReachLevel.length) {
            initLookupTables(level * 2);
        }
        return ExperienceManager.xpTotalToReachLevel[level];
    }

    static {
        ExperienceManager.hardMaxLevel = 100000;
        initLookupTables(25);
    }
}
