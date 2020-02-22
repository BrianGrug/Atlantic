package me.scifi.hcf.signs;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class KitSignListener implements Listener {

    private HCF plugin;

    public KitSignListener(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(e.getClickedBlock().getState() instanceof Sign){
                Sign sign = (Sign) e.getClickedBlock().getState();
                String[] lines = sign.getLines();
                if (lines[0].equalsIgnoreCase(Utils.chat("&9[Kit]")) && lines[1].equalsIgnoreCase("diamond")) {
                    Kits.giveDiamondKit(p);
                } else if(lines[0].equalsIgnoreCase(Utils.chat("&9[Kit]")) && lines[1].equalsIgnoreCase("archer")) {
                    Kits.giveArcherKit(p);
                } else if(lines[0].equalsIgnoreCase(Utils.chat("&9[Kit]")) && lines[1].equalsIgnoreCase("bard")) {
                    Kits.giveBardKit(p);
                } else if(lines[0].equalsIgnoreCase(Utils.chat("&9[Kit]")) && lines[1].equalsIgnoreCase("rogue")) {
                    Kits.giveRogueKit(p);
                } else if(lines[0].equalsIgnoreCase(Utils.chat("&9[Kit]")) && lines[1].equalsIgnoreCase("miner")) {
                    Kits.giveMinerKit(p);
                } else if(lines[0].equalsIgnoreCase(Utils.chat("&9[Kit]")) && lines[1].equalsIgnoreCase("builder")) {
                    Kits.giveBuilderKit(p);
                }
            }
        }
    }
}
