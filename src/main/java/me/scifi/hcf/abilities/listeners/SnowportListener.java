package me.scifi.hcf.abilities.listeners;

import me.scifi.hcf.Cooldown;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.abilities.Item;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SnowportListener implements Listener {

    private HCF plugin = HCF.getPlugin();

    private Item item = Item.SNOWPORT;

    private Cooldown cooldown = new Cooldown();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Snowball)) return;

        Snowball snowball = (Snowball) e.getEntity();

        if (!(snowball.getShooter() instanceof Player)) return;

        Player player = (Player) snowball.getShooter();

        if (!cooldown.isOnCooldown(player)) {

            if (!item.isSimilar(player.getItemInHand())) return;

            snowball.setMetadata("snowport", new FixedMetadataValue(plugin, player.getUniqueId()));
            cooldown.placeOnCooldown(player, item.getCooldown());

        } else {

            player.sendMessage(Utils.chat(plugin.getItemsYML().getString("snowport.remaining").replace("%remaining%", DurationFormatter.getRemaining(cooldown.getRemaining(player), true))));

            if (plugin.getItemsYML().getBoolean("snowport.refund"))
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);

            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Snowball)) return;

        Snowball snowball = (Snowball) e.getDamager();

        if (!(snowball.getShooter() instanceof Player) || !(e.getEntity() instanceof Player)) return;

        Player player = (Player) snowball.getShooter();

        Player damaged = (Player) e.getEntity();

        if (!snowball.hasMetadata("snowport")) return;

        if (plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone() || plugin.getFactionManager().getFactionAt(damaged.getLocation()).isSafezone())
            return;

        if (!plugin.getItemsYML().getBoolean("snowport.radius.enabled")) {
            Location shooterLocation = player.getLocation();
            player.teleport(damaged);
            damaged.teleport(shooterLocation);
            return;
        }


        if (damaged.getLocation().distance(player.getLocation()) > plugin.getItemsYML().getInt("snowport.radius.radius"))
            e.setCancelled(true);

        else {
            Location shooterLocation = player.getLocation();
            player.teleport(damaged);
            damaged.teleport(shooterLocation);
        }
    }

}
