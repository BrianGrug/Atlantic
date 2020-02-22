package me.scifi.hcf.faction.argument;

import com.doctordark.util.command.CommandArgument;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.faction.type.PlayerFaction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FactionTopArgument extends CommandArgument {

    private HCF plugin;

    public final Comparator<PlayerFaction> compareFactionPoints = Comparator.comparingLong(PlayerFaction::getPoints);


    public FactionTopArgument(HCF plugin){
        super("top", "Check which faction has the most points.");
    }

    @Override
    public String getUsage(String label) {
        return "&c/"+ label + " " + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> header = Utils.list(HCF.getPlugin().getMessagesYML().getStringList("FACTION-TOP-HEADER"));
        header.forEach(sender::sendMessage);
        List<PlayerFaction> list = HCF.getPlugin().getFactionManager().getFactions().stream().filter(f -> f instanceof PlayerFaction).map(f -> (PlayerFaction) f).filter(f -> f.getPoints() > 0).collect(Collectors.toList());
        if(list.isEmpty()){
            sender.sendMessage(Utils.chat(HCF.getPlugin().getMessagesYML().getString("FACTION-TOP-EMPTY")));
            return true;
        }
        list.sort(compareFactionPoints);
        Collections.reverse(list);
        for(int i = 0; i < 10; i++) {
            if(i >= list.size()){
                break;
            }
            PlayerFaction next = list.get(i);
            int facNumber = i + 1;
            sender.sendMessage(Utils.chat("&6" + facNumber + "." + " &c" + next.getName() + "&7: &f" + next.getPoints()) );
        }
        return false;
    }
}
