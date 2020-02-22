package me.scifi.hcf.command;

import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.FactionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LFACommand implements CommandExecutor {

    private HCF plugin;

    public LFACommand(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("lfa")){
          if(sender instanceof Player){
              Player p = (Player) sender;
              FactionManager fm = plugin.getFactionManager();
              if(p.hasPermission(command.getPermission())){
                if(fm.getPlayerFaction(p) != null) {
                    if (fm.getPlayerFaction(p).getAlliedFactions().size() >= plugin.getConfig().getInt("factions.max-allies")) {
                        p.sendMessage(Utils.chat(plugin.messagesYML.getString("FACTION-MAX-ALLIES")));
                        return true;
                    } else {
                        List<String> lfa_message = plugin.messagesYML.getStringList("LFA-MESSAGE");
                        if(!plugin.getLfa().isOnCooldown(p)){
                            lfa_message.forEach(str -> Bukkit.getServer().broadcastMessage(Utils.chat(str)
                            .replace("%FACTION%", fm.getPlayerFaction(p).getName())));
                            plugin.getLfa().placeOnCooldown(p,HCF.getPlugin().getConfig().getInt("LFA-COOLDOWN"));
                            return true;
                        } else {
                            p.sendMessage(Utils.chat("&cYou Are Already On Cooldown For &7" + DurationFormatter.getRemaining(plugin.getLfa().getRemaining(p),true)));
                        }
                    }


                } else {
                    sender.sendMessage(Utils.chat(plugin.messagesYML.getString("FACTION-NOT-FOUND")));
                    return true;
                }
              }
          }
      }
        return false;
    }
}
