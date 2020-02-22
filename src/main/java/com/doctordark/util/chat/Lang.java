package com.doctordark.util.chat;

import java.util.*;

import org.bukkit.*;

import java.util.logging.*;
import java.net.*;
import java.nio.charset.*;
import java.util.regex.*;
import java.io.*;

import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_7_R4.inventory.*;

import net.minecraft.server.v1_7_R4.*;

import com.google.common.base.*;

import org.bukkit.enchantments.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.*;
import org.bukkit.craftbukkit.v1_7_R4.potion.*;

@Deprecated
public class Lang {
    private static final Pattern PATTERN;
    private static final String HASH_17 = "03f31164d234f10a3230611656332f1756e570a9";
    private static Map<String, String> translations;

    public static void initialize(final String lang) throws IOException {
        Lang.translations = new HashMap<String, String>();
        Bukkit.getLogger().log(Level.INFO, "Initialling");
        if ("03f31164d234f10a3230611656332f1756e570a9".length() >= 2) {
            final String url = "http://resources.download.minecraft.net/" + "03f31164d234f10a3230611656332f1756e570a9".substring(0, 2) + "/" + "03f31164d234f10a3230611656332f1756e570a9";
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    Bukkit.getLogger().info(line);
                    if (line.contains("=")) {
                        final Matcher matcher = Lang.PATTERN.matcher(line);
                        if (!matcher.matches()) {
                            continue;
                        }
                        Lang.translations.put(matcher.group(1), matcher.group(2));
                    }
                }
            }
        }
    }

    public static String translatableFromStack(final ItemStack stack) {
        final net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        final Item item = nms.getItem();
        return item.a(nms) + ".name";
    }

    public static String fromStack(final ItemStack stack) {
        final String node = translatableFromStack(stack);
        return (String) MoreObjects.firstNonNull((Object) Lang.translations.get(node), (Object) node);
    }

    public static String translatableFromEnchantment(final Enchantment ench) {
        final net.minecraft.server.v1_7_R4.Enchantment nms = net.minecraft.server.v1_7_R4.Enchantment.byId[ench.getId()];
        return (nms == null) ? ench.getName() : nms.a();
    }

    public static String fromEnchantment(final Enchantment enchantment) {
        final String node = translatableFromEnchantment(enchantment);
        final String trans = Lang.translations.get(node);
        return (trans != null) ? trans : node;
    }

    public static String translatableFromPotionEffectType(final PotionEffectType effectType) {
        final CraftPotionEffectType craftType = (CraftPotionEffectType) PotionEffectType.getById(effectType.getId());
        return craftType.getHandle().a();
    }

    public static String fromPotionEffectType(final PotionEffectType effectType) {
        final String node = translatableFromPotionEffectType(effectType);
        final String val = Lang.translations.get(node);
        if (val == null) {
            return node;
        }
        return val;
    }

    public static String translate(final String key, final Object... args) {
        return String.format(Lang.translations.get(key), args);
    }

    static {
        PATTERN = Pattern.compile("^\\s*([\\w\\d\\.]+)\\s*=\\s*(.*)\\s*$");
    }
}
