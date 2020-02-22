package com.doctordark.util;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import java.util.*;

public final class InventoryUtils {
    public static final int DEFAULT_INVENTORY_WIDTH = 9;
    public static final int MINIMUM_INVENTORY_HEIGHT = 1;
    public static final int MINIMUM_INVENTORY_SIZE = 9;
    public static final int MAXIMUM_INVENTORY_HEIGHT = 6;
    public static final int MAXIMUM_INVENTORY_SIZE = 54;
    public static final int MAXIMUM_SINGLE_CHEST_SIZE = 27;
    public static final int MAXIMUM_DOUBLE_CHEST_SIZE = 54;

    public static ItemStack[] deepClone(final ItemStack[] origin) {
        Preconditions.checkNotNull((Object) origin, (Object) "Origin cannot be null");
        final ItemStack[] cloned = new ItemStack[origin.length];
        for (int i = 0; i < origin.length; ++i) {
            final ItemStack next = origin[i];
            cloned[i] = ((next == null) ? null : next.clone());
        }
        return cloned;
    }

    public static int getSafestInventorySize(final int initialSize) {
        return (initialSize + 8) / 9 * 9;
    }

    public static void removeItem(final Inventory inventory, final Material type, final short data, final int quantity) {
        final ItemStack[] contents = inventory.getContents();
        final boolean compareDamage = type.getMaxDurability() == 0;
        for (int i = quantity; i > 0; --i) {
            for (final ItemStack content : contents) {
                if (content != null) {
                    if (content.getType() == type) {
                        if (!compareDamage || content.getData().getData() == data) {
                            if (content.getAmount() <= 1) {
                                inventory.removeItem(new ItemStack[] { content });
                                break;
                            }
                            content.setAmount(content.getAmount() - 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static int countAmount(final Inventory inventory, final Material type, final short data) {
        final ItemStack[] contents = inventory.getContents();
        final boolean compareDamage = type.getMaxDurability() == 0;
        int counter = 0;
        for (final ItemStack item : contents) {
            if (item != null) {
                if (item.getType() == type) {
                    if (!compareDamage || item.getData().getData() == data) {
                        counter += item.getAmount();
                    }
                }
            }
        }
        return counter;
    }

    public static boolean isEmpty(final Inventory inventory) {
        return isEmpty(inventory, true);
    }

    public static boolean isEmpty(final Inventory inventory, final boolean checkArmour) {
        boolean result = true;
        final ItemStack[] contents2;
        ItemStack[] contents = contents2 = inventory.getContents();
        for (final ItemStack content : contents2) {
            if (content != null && content.getType() != Material.AIR) {
                result = false;
                break;
            }
        }
        if (!result) {
            return false;
        }
        if (checkArmour && inventory instanceof PlayerInventory) {
            final ItemStack[] armorContents;
            contents = (armorContents = ((PlayerInventory) inventory).getArmorContents());
            for (final ItemStack content : armorContents) {
                if (content != null && content.getType() != Material.AIR) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean clickedTopInventory(final InventoryDragEvent event) {
        final InventoryView view = event.getView();
        final Inventory topInventory = view.getTopInventory();
        if (topInventory == null) {
            return false;
        }
        boolean result = false;
        final Set<Map.Entry<Integer, ItemStack>> entrySet = event.getNewItems().entrySet();
        final int size = topInventory.getSize();
        for (final Map.Entry<Integer, ItemStack> entry : entrySet) {
            if (entry.getKey() < size) {
                result = true;
                break;
            }
        }
        return result;
    }
}
