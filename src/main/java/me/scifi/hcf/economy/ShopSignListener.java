package me.scifi.hcf.economy;

import com.doctordark.internal.com.doctordark.base.BasePlugin;
import com.doctordark.util.InventoryUtils;
import com.doctordark.util.JavaUtils;
import me.scifi.hcf.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import me.scifi.hcf.HCF;
import me.scifi.hcf.listener.Crowbar;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Listener that allows {@link Player}s to buy or sell items via signs.
 */
public class ShopSignListener implements Listener {

    private static final long SIGN_TEXT_REVERT_TICKS = 100L;
    private static final Pattern ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
    private String sell, buy;

    private final HCF plugin;

    public ShopSignListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            sell = Utils.chat("&c-Sell-");
            buy = Utils.chat("&a-Buy-");
            if (state instanceof Sign) {
                Sign sign = (Sign) state;
                String[] lines = sign.getLines();

                Integer quantity = JavaUtils.tryParseInt(lines[2]);
                if (quantity == null)
                    return;

                Integer price = JavaUtils.tryParseInt(ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
                if (price == null)
                    return;

                ItemStack stack;
                if (lines[1].equalsIgnoreCase("Crowbar")) {
                    stack = new Crowbar().getItemIfPresent();
                } else if ((stack = BasePlugin.getPlugin().getItemDb().getItem(ALPHANUMERIC_REMOVER.matcher(lines[1]).replaceAll(""), quantity)) == null) {
                    return;
                }

                // Final handling of shop.
                Player player = event.getPlayer();
                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                if (lines[0].equals(sell)) {
                        int sellQuantity = Math.min(quantity, InventoryUtils.countAmount(player.getInventory(), stack.getType(), stack.getDurability()));
                        if (sellQuantity <= 0) {
                            fakeLines[0] = ChatColor.RED + "Not carrying any";
                            fakeLines[2] = ChatColor.RED + "on you.";
                            fakeLines[3] = "";
                        } else {
                            // Recalculate the price.
                            int newPrice = (int) (((double) price / (double) quantity) * (double) sellQuantity);
                            fakeLines[0] = ChatColor.GREEN + "Sold " + sellQuantity;
                            fakeLines[3] = ChatColor.GREEN + "for " + EconomyManager.ECONOMY_SYMBOL + newPrice;

                            plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
                            InventoryUtils.removeItem(player.getInventory(), stack.getType(), stack.getData().getData(), sellQuantity);
                            player.updateInventory();
                        }
                    }  else if (lines[0].equals(buy)) {
                        if (price > plugin.getEconomyManager().getBalance(player.getUniqueId())) {
                            fakeLines[0] = ChatColor.RED + "Cannot afford";
                        } else {
                            fakeLines[0] = ChatColor.GREEN + "Item bought";
                            fakeLines[3] = ChatColor.GREEN + "for " + EconomyManager.ECONOMY_SYMBOL + price;
                            plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price);

                            World world = player.getWorld();
                            Location location = player.getLocation();
                            Map<Integer, ItemStack> excess = player.getInventory().addItem(stack);
                            for (Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
                                world.dropItemNaturally(location, excessItemStack.getValue());
                            }

                            player.setItemInHand(player.getItemInHand()); // resend held item packet.
                            player.updateInventory();
                            }

                    } else {
                        return;
                    }


                event.setCancelled(true);
                BasePlugin.getPlugin().getSignHandler().showLines(player, sign, fakeLines, SIGN_TEXT_REVERT_TICKS, true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignCreation(SignChangeEvent e) {
        Player p = e.getPlayer();
        String[] lines = e.getLines();
        if (p.hasPermission("hcf.utilities.admin")) {
            for (int i = 0; i < lines.length; i++) {
                if (e.getLine(i).equals("&c-Sell-") || e.getLine(i).equals("&a-Buy-") || e.getLine(i).equals("&9[Kit]")) {
                    e.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
                }
            }
        }
    }
}