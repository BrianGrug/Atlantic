package me.scifi.hcf.abilities.command;

import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.abilities.Item;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AbilityCommand implements CommandExecutor {

    private HCF plugin = HCF.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.chat("&cOnly players may execute this command."));
            return true;
        }

        Player player = (Player) sender;

        if (player.hasPermission(command.getPermission())) {
            if (args.length < 3) {
                sender.sendMessage(Utils.chat("&cUsage: /ability <player> <ability> <amount>"));
                return true;
            }

            if (!Item.getItemNames().contains(args[1].toUpperCase())) {
                sender.sendMessage(Utils.chat("&cThis ability does not currently exist"));
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.chat("&cPlayer is not online."));
                return true;
            }

            Item item = Item.getItem(args[1]);
            try {
                int amount = Integer.parseInt(args[2]);
                if (item != null) {
                    ItemStack stack = item.getStack();
                    stack.setAmount(amount);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(item.getDisplay());
                    meta.setLore(item.getLore());
                    stack.setItemMeta(meta);
                    target.getInventory().addItem(stack);
                    return true;
                }
            } catch (NumberFormatException e) {

            }
        }
        return false;
    }
}
