package me.scifi.hcf.listener;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.command.MuteChatCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteChatListener implements Listener {

    private HCF plugin;

    public MuteChatListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(MuteChatCommand.isLocked){
            if(!e.getPlayer().hasPermission("hcf.command.mutechat.bypass")){
                e.getPlayer().sendMessage(Utils.chat(plugin.messagesYML.getString("CHAT-MUTED-TALK")));
                e.setMessage("");
                e.setCancelled(true);
            }
        }
    }

}
