package com.doctordark.util.itemdb;

import org.bukkit.configuration.serialization.*;
import org.bukkit.*;
import org.bukkit.material.*;
import org.bukkit.inventory.*;
import java.util.*;
import com.doctordark.internal.com.doctordark.base.*;

public class ItemData implements ConfigurationSerializable {
    private final Material material;
    private final short itemData;

    public ItemData(final MaterialData data) {
        this(data.getItemType(), data.getData());
    }

    public ItemData(final ItemStack stack) {
        this(stack.getType(), stack.getData().getData());
    }

    public ItemData(final Material material, final short itemData) {
        this.material = material;
        this.itemData = itemData;
    }

    public ItemData(final Map<String, Object> map) {
        Object object = map.get("itemType");
        if (!(object instanceof String)) {
            throw new AssertionError((Object) "Incorrectly configurised");
        }
        this.material = Material.getMaterial((String) object);
        if ((object = map.get("itemData")) instanceof Short) {
            this.itemData = (short) object;
            return;
        }
        throw new AssertionError((Object) "Incorrectly configurised");
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("itemType", this.material.name());
        map.put("itemData", this.itemData);
        return map;
    }

    public Material getMaterial() {
        return this.material;
    }

    @Deprecated
    public short getItemData() {
        return this.itemData;
    }

    public String getItemName() {
        return BasePlugin.getPlugin().getItemDb().getName(new ItemStack(this.material, 1, this.itemData));
    }

    public static ItemData fromItemName(final String string) {
        final ItemStack stack = BasePlugin.getPlugin().getItemDb().getItem(string);
        return new ItemData(stack.getType(), stack.getData().getData());
    }

    public static ItemData fromStringValue(final String value) {
        final int firstBracketIndex = value.indexOf(40);
        if (firstBracketIndex == -1) {
            return null;
        }
        final int otherBracketIndex = value.indexOf(41);
        if (otherBracketIndex == -1) {
            return null;
        }
        final String itemName = value.substring(0, firstBracketIndex);
        final String itemData = value.substring(firstBracketIndex + 1, otherBracketIndex);
        final Material material = Material.getMaterial(itemName);
        return new ItemData(material, Short.parseShort(itemData));
    }

    @Override
    public String toString() {
        return String.valueOf(this.material.name()) + "(" + String.valueOf(this.itemData) + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ItemData itemData1 = (ItemData) o;
        return this.itemData == itemData1.itemData && this.material == itemData1.material;
    }

    @Override
    public int hashCode() {
        int result = (this.material != null) ? this.material.hashCode() : 0;
        result = 31 * result + this.itemData;
        return result;
    }
}
