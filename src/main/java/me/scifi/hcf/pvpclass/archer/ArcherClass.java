package me.scifi.hcf.pvpclass.archer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.DurationFormatter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import me.scifi.hcf.pvpclass.PvpClass;
import me.scifi.hcf.pvpclass.event.PvpClassUnequipEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a {@link PvpClass} used to buff Archer game-play such as Bows.
 */
public class ArcherClass extends PvpClass implements Listener {

    private static final PotionEffect ARCHER_CRITICAL_EFFECT = new PotionEffect(PotionEffectType.WITHER, 60, 0);

    private static final int MARK_TIMEOUT_SECONDS = 15;
    private static final int MARK_EXECUTION_LEVEL = 3;
    private static final float MINIMUM_FORCE = 0.5F;

    private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
    private static final long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    private final TObjectLongMap<UUID> archerSpeedCooldowns = new TObjectLongHashMap<>();

    private final Table<UUID, UUID, ArcherMark> marks = HashBasedTable.create(); // Key of the Players UUID who applied the mark, value as the Mark applied.
    private final HCF plugin;

    public ArcherClass(HCF plugin) {
        super("Archer", TimeUnit.SECONDS.toMillis(10L));
        this.plugin = plugin;

        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    public Map<UUID, ArcherMark> getSentMarks(Player player) {
        synchronized (marks) {
            return marks.column(player.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClassUnequip(PvpClassUnequipEvent event) {
        getSentMarks(event.getPlayer()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        getSentMarks(event.getEntity()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        getSentMarks(event.getPlayer()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        getSentMarks(event.getPlayer()).clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (!entity.equals(damager) && entity instanceof Player && damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            /*float force = arrow.getKnockbackStrength();
            if (force == -1.0) {
                return;
            }*/

            ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                Player shooter = (Player) source;
                if (plugin.getManagerHandler().getPvpClassManager().hasClassEquipped(shooter, this)) {
                    /*if (force <= MINIMUM_FORCE) {
                        shooter.sendMessage(ChatColor.RED + "Mark not applied as arrow was shot with less than " + MINIMUM_FORCE + "% force.");
                        return;
                    }*/

                    Player attacked = (Player) entity;
                    UUID attackedUUID = attacked.getUniqueId();

                    // Get the sent Mark, or create one.
                    Map<UUID, ArcherMark> givenMarks = getSentMarks(shooter);
                    ArcherMark archerMark = givenMarks.get(attackedUUID);
                    if (archerMark != null) {
                        archerMark.decrementTask.cancel();
                    } else {
                        givenMarks.put(attackedUUID, archerMark = new ArcherMark());
                    }

                    int newLevel = archerMark.incrementMark();
                    if (newLevel >= MARK_EXECUTION_LEVEL) {
                        event.setDamage(event.getDamage() + 5.0);
                        attacked.addPotionEffect(ARCHER_CRITICAL_EFFECT);

                        getSentMarks(shooter).clear();
                        archerMark.reset();

                        // Fake the effects.
                        World world = attacked.getWorld();
                        Location location = attacked.getLocation();
                        world.playEffect(location, Effect.EXPLOSION_HUGE, 4);
                        world.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);

                        attacked.sendMessage(ChatColor.GOLD + "Wipeout! " + ConfigurationService.ENEMY_COLOUR + shooter.getName() + ChatColor.GOLD + " hit you with a level " + ChatColor.WHITE
                                + ChatColor.GOLD + newLevel + " mark.");
                        shooter.sendMessage(ChatColor.YELLOW + "Wipeout! Hit " + ConfigurationService.ENEMY_COLOUR + attacked.getName() + ChatColor.YELLOW + " with a level " + ChatColor.WHITE
                                + newLevel + ChatColor.YELLOW + " mark.");
                    } else {
                        event.setDamage(event.getDamage() + 3.0);
                        shooter.sendMessage(ChatColor.YELLOW + "Now have a level " + ChatColor.WHITE + newLevel + ChatColor.YELLOW + " mark on " + ConfigurationService.ENEMY_COLOUR
                                + attacked.getName() + ChatColor.YELLOW + '.');

                        final ArcherMark finalMark = archerMark;
                        long ticks = MARK_TIMEOUT_SECONDS * 20L;
                        archerMark.decrementTask = new BukkitRunnable() {
                            @Override
                            public void run() {
                                int newLevel = finalMark.decrementMark();
                                if (newLevel == 0) {
                                    attacked.sendMessage(ConfigurationService.ENEMY_COLOUR + shooter.getName() + ChatColor.YELLOW + "'s mark on you has expired.");
                                    shooter.sendMessage(ChatColor.GOLD + "No longer have a mark on " + ConfigurationService.ENEMY_COLOUR + attacked.getName() + ChatColor.GOLD + '.');
                                    getSentMarks(shooter).remove(attacked.getUniqueId());
                                    cancel();
                                } else {
                                    attacked.sendMessage(ConfigurationService.ENEMY_COLOUR + shooter.getName() + ChatColor.GOLD + "'s mark on you has expired to level " + ChatColor.WHITE
                                            + ChatColor.GOLD + newLevel + '.');
                                    shooter.sendMessage(ChatColor.YELLOW + "Mark level on " + ConfigurationService.ENEMY_COLOUR + attacked.getName() + ChatColor.YELLOW + " is now " + ChatColor.WHITE
                                            + ChatColor.YELLOW + newLevel + '.');
                                }
                            }
                        }.runTaskTimer(plugin, ticks, ticks);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasItem() && event.getItem().getType() == Material.SUGAR) {
                if (plugin.getManagerHandler().getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                    return;
                }

                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();
                long timestamp = archerSpeedCooldowns.get(uuid);
                long millis = System.currentTimeMillis();
                long remaining = timestamp == archerSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
                if (remaining > 0L) {
                    player.sendMessage(ChatColor.RED + "Cannot use " + getName() + " speed for another " + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
                } else {
                    ItemStack stack = player.getItemInHand();
                    if (stack.getAmount() == 1) {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    } else
                        stack.setAmount(stack.getAmount() - 1);

                    plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_SPEED_EFFECT);
                    archerSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ARCHER_SPEED_COOLDOWN_DELAY);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteractWithFeather(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(plugin.getManagerHandler().getPvpClassManager().getEquippedClass(e.getPlayer()) == this  && e.hasItem() && e.getItem().getType() == Material.FEATHER){
                if(plugin.getArcherJumpBoost().isOnCooldown(e.getPlayer())){
                    e.getPlayer().sendMessage(Utils.chat("&cYou cannot use " + getName() + " jump boost for another " + DurationFormatUtils.formatDurationWords(plugin.getArcherJumpBoost().getRemaining(e.getPlayer()),true,true) + "."));
                } else {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP,100,2));
                    plugin.getArcherJumpBoost().placeOnCooldown(e.getPlayer(), plugin.getConfig().getLong("ARCHER-JUMPBOOST-COOLDOWN"));
                    e.getPlayer().getInventory().getItemInHand().setAmount(e.getPlayer().getInventory().getItemInHand().getAmount() - 1);
                }
            }
        }
    }

    @Override
    public boolean isApplicableFor(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET)
            return false;

        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE)
            return false;

        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS)
            return false;

        ItemStack boots = playerInventory.getBoots();
        return !(boots == null || boots.getType() != Material.LEATHER_BOOTS);
    }
}
