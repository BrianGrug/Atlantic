package me.scifi.hcf.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.EnderChest;

import java.util.Iterator;

/**
 * Listener that prevents the use of Ender Chests.
 */
public class EnderChestRemovalListener implements Listener {

    public EnderChestRemovalListener() {
        this.removeRecipe();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEnderChestOpen(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }

    /**
     * Removes the ender-chest crafting recipe from the server meaning it can only be obtained from creative mode.
     */
    private void removeRecipe() {
        for (Iterator<Recipe> iterator = Bukkit.recipeIterator(); iterator.hasNext();) {
            if (iterator.next().getResult().getType() == Material.ENDER_CHEST) {
                iterator.remove();
            }
        }
    }
}
