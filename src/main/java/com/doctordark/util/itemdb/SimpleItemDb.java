package com.doctordark.util.itemdb;

import gnu.trove.map.*;
import org.bukkit.plugin.java.*;
import com.google.common.collect.*;
import gnu.trove.map.hash.*;
import com.doctordark.util.*;
import org.bukkit.potion.*;
import org.bukkit.*;
import java.util.regex.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.*;
import java.util.*;
import org.apache.commons.lang3.*;

public class SimpleItemDb implements ItemDb {
    private static final Comparator<String> STRING_LENGTH_COMPARATOR;
    private final TObjectIntMap<String> items;
    private final TreeMultimap<ItemData, String> names;
    private final Map<ItemData, String> primaryName;
    private final TObjectShortMap<String> durabilities;
    private final ManagedFile file;
    private final Pattern splitPattern;
    private static final Pattern PARTS_PATTERN;

    public SimpleItemDb(final JavaPlugin plugin) {
        this.items = (TObjectIntMap<String>) new TObjectIntHashMap();
        this.names = (TreeMultimap<ItemData, String>) TreeMultimap.create((Comparator) Ordering.arbitrary(), (Comparator) SimpleItemDb.STRING_LENGTH_COMPARATOR);
        this.primaryName = new HashMap<ItemData, String>();
        this.durabilities = (TObjectShortMap<String>) new TObjectShortHashMap();
        this.splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");
        this.file = new ManagedFile("items.csv", plugin);
        this.reloadItemDatabase();
    }

    @Override
    public void reloadItemDatabase() {
        if (this.file.getFile() == null) {
            return;
        }
        final List<String> lines = this.file.getLines();
        if (lines.isEmpty()) {
            return;
        }
        this.durabilities.clear();
        this.items.clear();
        this.names.clear();
        this.primaryName.clear();
        for (String line : lines) {
            line = line.trim().toLowerCase(Locale.ENGLISH);
            if (line.length() > 0 && line.charAt(0) == '#') {
                continue;
            }
            final String[] parts = SimpleItemDb.PARTS_PATTERN.split(line);
            if (parts.length < 2) {
                continue;
            }
            Material material;
            try {
                final int numeric = Integer.parseInt(parts[1]);
                material = Material.getMaterial(numeric);
            } catch (IllegalArgumentException ex) {
                material = Material.getMaterial(parts[1]);
            }
            final short data = (short) ((parts.length > 2 && !parts[2].equals("0")) ? Short.parseShort(parts[2]) : 0);
            final String itemName = parts[0].toLowerCase(Locale.ENGLISH);
            this.durabilities.put(itemName, data);
            this.items.put(itemName, material.getId());
            final ItemData itemData = new ItemData(material, data);
            if (this.names.containsKey((Object) itemData)) {
                this.names.get(itemData).add(itemName);
            } else {
                this.names.put(itemData, itemName);
                this.primaryName.put(itemData, itemName);
            }
        }
    }

    @Override
    public ItemStack getPotion(final String id) {
        return this.getPotion(id, 1);
    }

    @Override
    public ItemStack getPotion(String id, final int quantity) {
        int length = id.length();
        if (length <= 1) {
            return null;
        }
        boolean splash = false;
        if (length > 1 && id.endsWith("s")) {
            id = id.substring(0, --length);
            splash = true;
            if (length <= 1) {
                return null;
            }
        }
        boolean extended = false;
        if (id.endsWith("e")) {
            id = id.substring(0, --length);
            extended = true;
            if (length <= 1) {
                return null;
            }
        }
        final Integer level = JavaUtils.tryParseInt(id.substring(length - 1, length));
        id = id.substring(0, --length);
        final String lowerCase = id.toLowerCase(Locale.ENGLISH);
        PotionType type = null;
        switch (lowerCase) {
        case "hp": {
            type = PotionType.FIRE_RESISTANCE;
            break;
        }
        case "rp": {
            type = PotionType.REGEN;
            break;
        }
        case "dp": {
            type = PotionType.INSTANT_DAMAGE;
            break;
        }
        case "swp": {
            type = PotionType.SPEED;
            break;
        }
        case "slp": {
            type = PotionType.SLOWNESS;
            break;
        }
        case "strp": {
            type = PotionType.STRENGTH;
            break;
        }
        case "wp": {
            type = PotionType.WEAKNESS;
            break;
        }
        case "pp": {
            type = PotionType.POISON;
            break;
        }
        case "frp": {
            type = PotionType.FIRE_RESISTANCE;
            break;
        }
        case "invp": {
            type = PotionType.INVISIBILITY;
            break;
        }
        case "nvp": {
            type = PotionType.NIGHT_VISION;
            break;
        }
        default: {
            return null;
        }
        }
        if (level == null || level > type.getMaxLevel()) {
            return null;
        }
        final Potion potion = new Potion(type);
        potion.setLevel((int) level);
        potion.setSplash(splash);
        potion.setHasExtendedDuration(extended);
        final ItemStack result = potion.toItemStack(quantity);
        result.setDurability((short) (result.getDurability() + 8192));
        return result;
    }

    @Override
    public ItemStack getItem(final String id) {
        final ItemStack result = this.getItem(id, 1);
        if (result == null) {
            return null;
        }
        result.setAmount(result.getMaxStackSize());
        return result;
    }

    @Override
    public ItemStack getItem(final String id, final int quantity) {
        ItemStack result = this.getPotion(id, quantity);
        if (result != null) {
            return result;
        }
        int itemId = 0;
        short metaData = 0;
        final Matcher parts = this.splitPattern.matcher(id);
        String itemName;
        if (parts.matches()) {
            itemName = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemName = id;
        }
        Integer last;
        if ((last = JavaUtils.tryParseInt(itemName)) != null) {
            itemId = last;
        } else if ((last = JavaUtils.tryParseInt(id)) != null) {
            itemId = last;
        } else {
            itemName = itemName.toLowerCase(Locale.ENGLISH);
        }
        if (itemId < 1) {
            if (this.items.containsKey((Object) itemName)) {
                itemId = this.items.get((Object) itemName);
                if (this.durabilities.containsKey((Object) itemName) && metaData == 0) {
                    metaData = this.durabilities.get((Object) itemName);
                }
            } else if (Material.getMaterial(itemName.toUpperCase(Locale.ENGLISH)) != null) {
                final Material bMaterial = Material.getMaterial(itemName.toUpperCase(Locale.ENGLISH));
                itemId = bMaterial.getId();
            } else {
                try {
                    final Material bMaterial = Bukkit.getUnsafe().getMaterialFromInternalName(itemName.toLowerCase(Locale.ENGLISH));
                    itemId = bMaterial.getId();
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        if (itemId < 1) {
            return null;
        }
        final Material mat = Material.getMaterial(itemId);
        if (mat == null) {
            return null;
        }
        result = new ItemStack(mat);
        result.setAmount(quantity);
        result.setDurability(metaData);
        return result;
    }

    @Override
    public List<ItemStack> getMatching(final Player player, final String[] args) {
        final List<ItemStack> items = new ArrayList<ItemStack>();
        final PlayerInventory inventory = player.getInventory();
        if (args.length < 1 || args[0].equalsIgnoreCase("hand")) {
            items.add(player.getItemInHand());
        } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) {
            for (final ItemStack stack : inventory.getContents()) {
                if (stack != null && stack.getType() != Material.AIR) {
                    items.add(stack);
                }
            }
        } else if (args[0].equalsIgnoreCase("blocks")) {
            for (final ItemStack stack : inventory.getContents()) {
                if (stack != null && stack.getType() != Material.AIR && stack.getType().isBlock()) {
                    items.add(stack);
                }
            }
        } else {
            items.add(this.getItem(args[0]));
        }
        if (items.isEmpty() || items.get(0).getType() == Material.AIR) {
            return null;
        }
        return items;
    }

    @Override
    public String getName(final ItemStack item) {
        return CraftItemStack.asNMSCopy(item).getName();
    }

    @Deprecated
    @Override
    public String getPrimaryName(final ItemStack item) {
        ItemData itemData = new ItemData(item.getType(), item.getDurability());
        String name = this.primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getType(), (short) 0);
            name = this.primaryName.get(itemData);
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    @Override
    public String getNames(final ItemStack item) {
        ItemData itemData = new ItemData(item.getType(), item.getDurability());
        Collection<String> nameList = (Collection<String>) this.names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(item.getType(), (short) 0);
            nameList = (Collection<String>) this.names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }
        List<String> list = new ArrayList<String>(nameList);
        if (nameList.size() > 15) {
            list = list.subList(0, 14);
        }
        return StringUtils.join((Iterable) list, ", ");
    }

    static {
        STRING_LENGTH_COMPARATOR = new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.length() - o2.length();
            }
        };
        PARTS_PATTERN = Pattern.compile("[^a-z0-9]");
    }
}
