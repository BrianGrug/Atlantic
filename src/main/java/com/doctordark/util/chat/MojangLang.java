package com.doctordark.util.chat;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import net.minecraft.server.v1_7_R4.Item;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_7_R4.potion.CraftPotionEffectType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import com.doctordark.internal.com.doctordark.base.GuavaCompat;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class provides methods to translate enchantments, items and other
 * translatable strings into the language of Minecraft, as well as retrieving the
 * translatable representations of Items and Enchantments to be sent to the client
 * using raw chat message.
 */
public abstract class MojangLang {

    protected static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    protected static final Splitter SPLITTER = Splitter.on('=');

    protected final Table<Locale, String, String> translations = HashBasedTable.create();

    /**
     * Reads the translations of the server for a specific Minecraft version and locale.
     *
     * @param minecraftVersion the Minecraft version, e.g. 1.7.10
     * @param locale           the locale to translate from
     * @throws IOException              if the source could not be read
     * @throws IllegalArgumentException if the Locale for the given Minecraft version is already indexed
     */
    public void index(String minecraftVersion, @Nullable Locale locale) throws IllegalArgumentException, IOException {
        if (locale == null) locale = Locale.ENGLISH;

        checkArgument(!translations.contains(locale, minecraftVersion), "Already indexed Minecraft version '" + minecraftVersion + "' for locale '" + locale.toLanguageTag() + "'.");
    }

    void finallyIndex(Locale locale, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("=")) {
                Iterable<String> iterable = SPLITTER.split(line.trim());
                if (Iterables.size(iterable) == 2) {
                    this.translations.put(locale, Iterables.get(iterable, 0), Iterables.get(iterable, 1));
                }
            }
        }
    }

    /**
     * Translates the provided translatable string into Mojang language of a Locale,
     * <p>
     * If args are provided, the translated string must contain the same number of
     * placeholders, the args will be substituted using String.format()
     * eg. {@link MojangLang}.translate("record.nowPlaying", "My favourite Song") -> "Now playing: My Favourite Song"
     *
     * @param locale the locale to translate to
     * @param key    the translatable string
     * @param args   objects to be formatted into the translated string
     * @return the translated string
     */
    public String translate(Locale locale, String key, Object... args) {
        checkArgument(translations.containsRow(locale), "Translations for locale " + locale.toLanguageTag() + " not initialised, use #index(String, Locale) to index");
        return String.format(translations.get(locale, key), args);
    }

    /**
     * get the translatable string representing the name of the provided item stack.
     * eg.
     * Lang.translatableFromStack(new ItemStack(Material.INK_SACK, 1, 4)) = "item.dye.blue"
     *
     * @param stack a Bukkit ItemStack
     * @return the translatable string representing the item name.
     */
    public String translatableFromStack(ItemStack stack) {
        net.minecraft.server.v1_7_R4.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        Item item = nms.getItem();
        return item.a(nms) + ".name";
    }

    /**
     * get the name of the provided item translated to Mojang's language
     * eg. {@link MojangLang}.fromStack(new ItemStack(Material.INK_SACK, 1, 4)) = "Lapis Lazuli"
     *
     * @param stack a Bukkit ItemStack
     * @return translated name of the item.
     */
    public String fromStack(ItemStack stack) {
        String node = translatableFromStack(stack);
        return MoreObjects.firstNonNull(translations.get(DEFAULT_LOCALE, node), node);
    }

    /**
     * get the translatable string representing the name of the provided enchantment
     * eg. {@link MojangLang}.translatableFromEnchantment(Enchantment.DIG_SPEED) = "enchantment.digging"
     *
     * @param enchantment a Bukkit Enchantment
     * @return the translatable string representing the name of the enchantment
     */
    public String translatableFromEnchantment(Enchantment enchantment) {
        net.minecraft.server.v1_7_R4.Enchantment nms = net.minecraft.server.v1_7_R4.Enchantment.byId[enchantment.getId()];
        return nms == null ? enchantment.getName() : nms.a();
    }

    /**
     * Gets the name of the provided enchantment translated to the Mojang's language
     * eg. {@link MojangLang}.fromEnchantment({@link Enchantment#DIG_SPEED} = "enchantment.digging"
     *
     * @param enchantment a Bukkit Enchantment
     * @return translated name of the enchantment.
     */
    public String fromEnchantment(Enchantment enchantment) {
        String node = translatableFromEnchantment(enchantment);
        return GuavaCompat.firstNonNull(translations.get(DEFAULT_LOCALE, node), node);
    }

    /**
     * Gets the translatable string representing the name of the provided potion effect.
     * eg. {@link MojangLang}.translatableFromEnchantment(PotionEffectType.FAST_DIGGING) = "potion.digSpeed"
     *
     * @param effectType a PotionEffectType
     * @return the translatable string representing the name of the potion effect.
     */
    public static String translatableFromPotionEffectType(PotionEffectType effectType) {
        CraftPotionEffectType craftType = (CraftPotionEffectType) PotionEffectType.getById(effectType.getId());
        return craftType.getHandle().a();
    }

    /**
     * Gets the name of the provided Potion Effect translated to the Mojang's language
     * eg. {@link MojangLang}.fromPotionEffectType({@link PotionEffectType#FAST_DIGGING}) = "potion.digSpeed"
     *
     * @param effectType a PotionEffectType
     * @return translated name of the Potion Effect.
     */
    public String fromPotionEffectType(PotionEffectType effectType) {
        String node = translatableFromPotionEffectType(effectType);
        String val = translations.get(DEFAULT_LOCALE, node);
        return val == null ? node : val;
    }

    /**
     * Translates the provided translatable string into the Mojang's language,
     * <p>
     * if args are provided, the translated string must contain the same number of
     * placeholders, the args will be substituted using String.format()
     * eg. {@link MojangLang}.translate("record.nowPlaying", "My favourite Song") -> "Now playing: My Favourite Song"
     *
     * @param key  the translatable string. eg.
     * @param args objects to be formatted into the translated string.
     * @return the translated string.
     */
    public String translate(String key, Object... args) {
        return String.format(translations.get(DEFAULT_LOCALE, key), args);
    }
}
