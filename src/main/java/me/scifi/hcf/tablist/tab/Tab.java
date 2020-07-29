package me.scifi.hcf.tablist.tab;

import me.scifi.hcf.tablist.Azazel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Tab {

    @Getter private Scoreboard scoreboard;
    @Getter @Setter private Team elevatedTeam;
    private Map<TabEntryPosition, String> entries;

    public Tab(Player player, boolean hook, Azazel azazel) {
        this.entries = new ConcurrentHashMap<>();

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (hook && !player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = player.getScoreboard();
        }

        elevatedTeam = scoreboard.registerNewTeam(getBlanks().get(getBlanks().size() - 1));

        for (Player other : Bukkit.getServer().getOnlinePlayers()) {

            getElevatedTeam(other, azazel).addEntry(other.getName());

            Tab tab = azazel.getTabByPlayer(other);
            if (tab != null) {
                tab.getElevatedTeam(player, azazel).addEntry(player.getName());
            }

            PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)other).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        }

        player.setScoreboard(scoreboard);

        initialize(player);
    }

    public Team getElevatedTeam(Player player, Azazel azazel) {
        if (player.hasMetadata("HydrogenPrefix")) {
            String prefix = ChatColor.getLastColors(player.getDisplayName().replace(ChatColor.RESET + "", ""));

            String name = getBlanks().get(getBlanks().size() - 1) + prefix;
            if (name.length() > 16) {
                name = name.substring(0, 15);
            }

            Team team = scoreboard.getTeam(name);

            if (team == null) {
                team = scoreboard.registerNewTeam(name);

                team.setPrefix(prefix);

            }

            return team;
        }

        return elevatedTeam;
    }

    public Set<TabEntryPosition> getPositions() {
        return entries.keySet();
    }

    public Team getByLocation(int x, int y) {
        for (TabEntryPosition position : entries.keySet()) {
            if (position.getX() == x && position.getY() == y) {
                return scoreboard.getTeam(position.getKey());
            }
        }
        return null;
    }

    private void initialize(Player player) {
        if (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 20; y++) {
                    String key = getNextBlank();
                    TabEntryPosition position = new TabEntryPosition(x, y, key, scoreboard);

                    entries.put(position, key);

                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getPlayerPacket(entries.get(position)));

                    Team team = scoreboard.getTeam(position.getKey());

                    if (team == null) {
                        team = scoreboard.registerNewTeam(position.getKey());
                    }

                    team.addEntry(entries.get(position));
                }
            }
        } else {
            for (int i = 0; i < 60; i++) {
                int x = i % 3;
                int y = i / 3;

                String key = getNextBlank();
                TabEntryPosition position = new TabEntryPosition(x, y, key, scoreboard);
                entries.put(position, key);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getPlayerPacket(entries.get(position)));

                Team team = scoreboard.getTeam(position.getKey());

                if (team == null) {
                    team = scoreboard.registerNewTeam(position.getKey());
                }

                team.addEntry(entries.get(position));
            }
        }
    }

    private String getNextBlank() {
        outer: for (String blank : getBlanks()) {

            if (scoreboard.getTeam(blank) != null) {
                continue;
            }

            for (String identifier : entries.values()) {
                if (identifier.equals(blank)) {
                    continue outer;
                }
            }
            return blank;
        }
        return null;
    }

    public List<String> getBlanks() {
        List<String> toReturn = new ArrayList<>();

        for (ChatColor color : ChatColor.values()) {
            for (int i = 0; i < 4; i++) {

                String identifier = StringUtils.repeat(color + "", 4 - i) + ChatColor.RESET;
                toReturn.add(identifier);
            }
        }

        return toReturn;
    }


    /*
        There should be a better way to do this without reflection
     */
    private static Packet getPlayerPacket(String name) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        Field action;
        Field username;
        Field player;
        try {
            action = PacketPlayOutPlayerInfo.class.getDeclaredField("action");
            username = PacketPlayOutPlayerInfo.class.getDeclaredField("username");
            player = PacketPlayOutPlayerInfo.class.getDeclaredField("player");

            action.setAccessible(true);
            username.setAccessible(true);
            player.setAccessible(true);

            action.set(packet, 0);
            username.set(packet, name);
            player.set(packet, new GameProfile(UUID.randomUUID(), name));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public static class UpdatedPacketPlayOutPlayerInfo extends PacketPlayOutPlayerInfo {

    }

    public static class TabEntryPosition {
        @Getter private final int x, y;
        @Getter private final String key;

        public TabEntryPosition(int x, int y, String key, Scoreboard scoreboard) {
            this.x = x;
            this.y = y;
            this.key = key;
        }
    }

}
