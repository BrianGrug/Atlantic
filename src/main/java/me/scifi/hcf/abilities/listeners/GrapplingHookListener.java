package me.scifi.hcf.abilities.listeners;

import me.scifi.hcf.Cooldown;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.abilities.Item;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

public class GrapplingHookListener implements Listener {

    private HCF plugin = HCF.getPlugin();

    private Item item = Item.GRAPPLINGHOOK;

    private Cooldown cooldown = new Cooldown();

    @EventHandler
    public void onFish(PlayerFishEvent e){

        Player player = e.getPlayer();

        if(!item.isSimilar(player.getItemInHand())) return;

        if(cooldown.isOnCooldown(player)) {
            player.sendMessage(Utils.chat(plugin.getItemsYML().getString("grapplinghook.remaining").replace("%remaining%", DurationFormatter.getRemaining(cooldown.getRemaining(player), true))));
            return;
        }

        if(!e.getHook().isValid()) return;
        player.getLocation().add(0,2,0);
        Vector vector = new Vector(e.getHook().getLocation().getBlockX(),0, e.getHook().getLocation().getBlockZ());
        player.setVelocity(vector);
        cooldown.placeOnCooldown(player,item.getCooldown());


    }

}
