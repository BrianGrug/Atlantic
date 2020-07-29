package me.scifi.hcf.listener;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.command.MuteChatCommand;
import me.scifi.hcf.faction.FactionManager;
import me.scifi.hcf.faction.struct.ChatChannel;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Set;

public class ChatListener implements Listener {

    private HCF plugin;

    public ChatListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage().replace("%", "%%");
        FactionManager fm = plugin.getManagerHandler().getFactionManager();
        String rankName = plugin.getRank().getGroupPrefix(p);
        ChatChannel chatChannel = fm.getPlayerFaction(p) == null ? ChatChannel.PUBLIC : fm.getPlayerFaction(p).getMember(p).getChatChannel();
        if (MuteChatCommand.isLocked && !p.hasPermission("hcf.command.mutechat.bypass")) {
            p.sendMessage(Utils.chat(plugin.getMessagesYML().getString("CHAT-MUTED-TALK")));
            e.setCancelled(true);
        }
        if (chatChannel == ChatChannel.PUBLIC) {
            if (fm.getPlayerFaction(p) == null) {
                e.setFormat(Utils.chat("&7[&c*&7] " + rankName + p.getName() + "&7: &f") + message);
            } else {
                e.setFormat(Utils.chat("&7[&c" + fm.getPlayerFaction(p).getName() + "&7] " + rankName + p.getName() + "&7: &f") + message);
            }
        } else if (chatChannel == ChatChannel.FACTION) {
            Set<Player> recipients = fm.getPlayerFaction(p).getOnlinePlayers();
            String formatted = Utils.chat("&3(Team) " + p.getName() + ": &e") + message;
            e.setCancelled(true);
            recipients.forEach(player -> player.sendMessage(formatted));
        } else if (chatChannel == ChatChannel.ALLIANCE) {
            Set<Player> recipients = fm.getPlayerFaction(p).getOnlinePlayers();
            Collection<PlayerFaction> allied = fm.getPlayerFaction(p).getAlliedFactions();
            for (PlayerFaction af : allied) {
                recipients.addAll(af.getOnlinePlayers());
            }
            String formatted = Utils.chat("&9(Ally) " + p.getName() + ": &e") + message;
            e.setCancelled(true);
            recipients.forEach(player -> player.sendMessage(formatted));
        }
    }

}
