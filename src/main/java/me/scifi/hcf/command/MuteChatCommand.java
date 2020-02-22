package me.scifi.hcf.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteChatCommand implements CommandExecutor {

    private HCF plugin;

    public static boolean isLocked = false;

    public MuteChatCommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(command.getPermission())){
          if(isLocked){
              isLocked = false;
              sender.sendMessage(Utils.chat(plugin.messagesYML.getString("MUTE-CHAT-DISABLED")));
              Bukkit.getServer().broadcastMessage(Utils.chat(plugin.messagesYML.getString("CHAT-ENABLED-BROADCAST")));
              return true;
          }
          isLocked = true;
          sender.sendMessage(Utils.chat(plugin.messagesYML.getString("MUTE-CHAT-ENABLED")));
          Bukkit.getServer().broadcastMessage(Utils.chat(plugin.messagesYML.getString("CHAT-DISABLED-BROADCAST")));
        }
        return false;
    }
}
