package me.scifi.hcf.abilities.listeners;

import me.scifi.hcf.Cooldown;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.abilities.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BelchbombListener implements Listener {

    private HCF plugin = HCF.getPlugin();

    private Item item = Item.BELCHBOMB;

    private Cooldown cooldown = new Cooldown();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        if (!e.getAction().name().contains("RIGHT")) return;

        int radius = plugin.getItemsYML().getInt("belchbomb.radius");

        if (!item.isSimilar(player.getItemInHand())) return;

        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(Utils.chat(plugin.getItemsYML().getString("belchbomb.remaining").replace("%remaining%", DurationFormatter.getRemaining(cooldown.getRemaining(player), true))));
            return;
        }

        if(plugin.getManagerHandler().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) return;

        player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player)
                .filter(entity -> !entity.getUniqueId().equals(player.getUniqueId()))
                .forEach(players -> {
                    if(!plugin.getManagerHandler().getFactionManager().getFactionAt(players.getLocation()).isSafezone()) {
                        ((Player) players).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, plugin.getItemsYML().getInt("belchbomb.duration") * 20, plugin.getItemsYML().getInt("belchbomb.amplifier")));
                        ((Player) players).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, plugin.getItemsYML().getInt("belchbomb.duration") * 20, plugin.getItemsYML().getInt("belchbomb.amplifier")));
                    }
                });
        if(player.getItemInHand().getAmount() < 1) player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

        else {
            player.setItemInHand(new ItemStack(Material.AIR,1));
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,plugin.getItemsYML().getInt("belchbomb.selfeffect.duration") * 20,plugin.getItemsYML().getInt("belchbomb.selfeffect.amplifier")));
        cooldown.placeOnCooldown(player, item.getCooldown());
    }

}
