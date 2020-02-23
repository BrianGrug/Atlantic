package me.scifi.hcf;

import com.comphenix.protocol.concurrency.AbstractIntervalTree.Entry;
import com.doctordark.util.Config;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ConfigurationService {

    public static Config config;

    public static void init(JavaPlugin plugin) {

        plugin.getLogger().info("Loading settings.yml...");
        config = new Config(plugin, "settings",plugin.getDataFolder().getAbsolutePath());

        DISABLE_OBSIDIAN_GENERATORS = config.getBoolean("disable_obsidian_generators", DISABLE_OBSIDIAN_GENERATORS);

        SERVER_TIME_ZONE = TimeZone.getTimeZone(config.getString("server_time_zone", DEFAULT_SERVER_TIME_ZONE));
        WARZONE_RADIUS = config.getInt("warzone_radius", WARZONE_RADIUS);
        SPAWN_BUFFER = config.getInt("spawn_buffer", SPAWN_BUFFER);

        KIT_MAP = config.getBoolean("kit_map", KIT_MAP);
        List<String> disallowedFactionNames = config.getStringList("disallowed_faction_names");
        DISALLOWED_FACTION_NAMES = (disallowedFactionNames != null ? disallowedFactionNames : DISALLOWED_FACTION_NAMES);

        ConfigurationSection enc = config.getConfigurationSection("enchantment_limits");
        if (enc != null) {
            ENCHANTMENT_LIMITS.clear();
            for (String key : enc.getKeys(false)) {
                Enchantment e = Enchantment.getByName(key);
                if (e != null)
                    ENCHANTMENT_LIMITS.put(e, enc.getInt(key));
                else
                    plugin.getLogger().severe("Could not find enchantment with name " + key);
            }
        }
        for (java.util.Map.Entry<Enchantment, Integer> e : ENCHANTMENT_LIMITS.entrySet()) {
            plugin.getLogger().info(e.getKey().getName() + " : " + e.getValue());
        }

        ConfigurationSection pot = config.getConfigurationSection("potion_limits");
        if (pot != null) {
            POTION_LIMITS.clear();
            for (String key : pot.getKeys(false)) {
                PotionType potion = PotionType.valueOf(key);
                if (potion != null)
                    POTION_LIMITS.put(potion, pot.getInt(key));
                else
                    plugin.getLogger().severe("Could not find potion with name " + key);
            }
        }
        for (java.util.Map.Entry<PotionType, Integer> e : POTION_LIMITS.entrySet()) {
            plugin.getLogger().info(e.getKey().name() + " : " + e.getValue());
        }


        ConfigurationSection bor = config.getConfigurationSection("border_sizes");
        if (bor != null) {
            BORDER_SIZES.clear();
            for (String key : bor.getKeys(false)) {
                World.Environment env = World.Environment.valueOf(key);
                if (env != null)
                    BORDER_SIZES.put(env, bor.getInt(key));
                else
                    plugin.getLogger().severe("Could not find world type with name " + key);
            }
        }


        DIAMOND_ORE_ALERTS = config.getBoolean("diamond_ore_alerts", DIAMOND_ORE_ALERTS);

        SUBCLAIM_NAME_CHARACTERS_MIN = config.getInt("subclaim_name_characters_min", SUBCLAIM_NAME_CHARACTERS_MIN);
        SUBCLAIM_NAME_CHARACTERS_MAX = config.getInt("subclaim_name_characters_max", SUBCLAIM_NAME_CHARACTERS_MAX);

        FACTION_NAME_CHARACTERS_MIN = config.getInt("faction_name_characters_min", FACTION_NAME_CHARACTERS_MIN);
        FACTION_NAME_CHARACTERS_MAX = config.getInt("faction_name_characters_max", FACTION_NAME_CHARACTERS_MAX);
        MAX_MEMBERS_PER_FACTION = config.getInt("max_members_per_faction", MAX_MEMBERS_PER_FACTION);

        END_PORTAL_RADIUS = config.getInt("end_portal_radius", END_PORTAL_RADIUS);
        END_PORTAL_CENTER = config.getInt("end_portal_center", END_PORTAL_CENTER);

        DEFAULT_DEATHBAN_DURATION = config.getLong("default_deathban_duration", DEFAULT_DEATHBAN_DURATION);

        TEAMMATE_COLOUR = ChatColor.valueOf(config.getString("teammate_colour"));
        ALLY_COLOUR = ChatColor.valueOf(config.getString("ally_colour"));
        ENEMY_COLOUR = ChatColor.valueOf(config.getString("enemy_colour"));

        FACTION_MUTE_COMMAND = config.getString("faction_mute_command");

        SAFEZONE_COLOUR = ChatColor.valueOf(config.getString("safezone_colour"));
        ROAD_COLOUR = ChatColor.valueOf(config.getString("road_colour"));
        WARZONE_COLOUR = ChatColor.valueOf(config.getString("warzone_colour"));
        WILDERNESS_COLOUR = ChatColor.valueOf(config.getString("wilderness_colour"));

        MAX_ALLIES_PER_FACTION = config.getInt("max_allies_per_faction", MAX_ALLIES_PER_FACTION);
        MAX_CLAIMS_PER_FACTION = config.getInt("max_claims_per_faction", MAX_CLAIMS_PER_FACTION);
        ALLOW_CLAIMING_BESIDES_ROADS = config.getBoolean("allow_claiming_besides_roads", ALLOW_CLAIMING_BESIDES_ROADS);

        DTR_MILLIS_BETWEEN_UPDATES = config.getLong("dtr_millis_between_updates", DTR_MILLIS_BETWEEN_UPDATES);
        // DTR words not configurable

        DTR_INCREMENT_BETWEEN_UPDATES = config.getDouble("dtr_increment_between_updates", DTR_INCREMENT_BETWEEN_UPDATES);
        MAXIMUM_DTR = config.getDouble("maximum_dtr", MAXIMUM_DTR);

        EXP_MULTIPLIER_GENERAL = config.getDouble("exp_multiplier_general", EXP_MULTIPLIER_GENERAL);
        EXP_MULTIPLIER_FISHING = config.getDouble("exp_multiplier_fishing", EXP_MULTIPLIER_FISHING);
        EXP_MULTIPLIER_SMELTING = config.getDouble("exp_multiplier_smelting", EXP_MULTIPLIER_SMELTING);
        EXP_MULTIPLIER_LOOTING_PER_LEVEL = config.getDouble("exp_multiplier_looting_per_level", EXP_MULTIPLIER_LOOTING_PER_LEVEL);
        EXP_MULTIPLIER_LUCK_PER_LEVEL = config.getDouble("exp_multiplier_luck_per_level", EXP_MULTIPLIER_LUCK_PER_LEVEL);
        EXP_MULTIPLIER_FORTUNE_PER_LEVEL = config.getDouble("exp_multiplier_fortune_per_level", EXP_MULTIPLIER_FORTUNE_PER_LEVEL);

        CONQUEST_POINT_LOSS_PER_DEATH = config.getInt("conquest_point_loss_per_death", CONQUEST_POINT_LOSS_PER_DEATH);
        CONQUEST_REQUIRED_WIN_POINTS = config.getInt("conquest_required_win_points", CONQUEST_REQUIRED_WIN_POINTS);

        FOUND_DIAMONDS_ALERTS = config.getBoolean("found_diamonds_alerts", FOUND_DIAMONDS_ALERTS);
        COMBAT_LOG_DESPAWN_TICKS = config.getLong("combat_log_despawn_ticks", COMBAT_LOG_DESPAWN_TICKS);
        COMBAT_LOG_PREVENTION_ENABLED = config.getBoolean("combat_log_prevention_enabled", COMBAT_LOG_PREVENTION_ENABLED);

        plugin.getLogger().info("Loaded settings.yml.");
    }

    public static boolean DISABLE_OBSIDIAN_GENERATORS = true;

    public static String FACTION_MUTE_COMMAND = "mute %player% 1h Toxic faction -S";

    private static String DEFAULT_SERVER_TIME_ZONE = "GMT+1";
    public static TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone(DEFAULT_SERVER_TIME_ZONE);
    public static int WARZONE_RADIUS = 850;
    public static  int SPAWN_BUFFER = 128;

    public static boolean KIT_MAP = false;

    public static List<String> DISALLOWED_FACTION_NAMES = ImmutableList.of("kohieotw", "kohisotw", "hcteams", "hcteamseotw", "hcteamssotw", "para", "parahcf", "parasotw", "paraeotw");

    public static Map<Enchantment, Integer> ENCHANTMENT_LIMITS = new HashMap<>();
    public static Map<PotionType, Integer> POTION_LIMITS = new EnumMap<>(PotionType.class);
    public static final Map<World.Environment, Integer> BORDER_SIZES = new EnumMap<>(World.Environment.class);
    public static Map<World.Environment, Integer> SPAWN_RADIUS_MAP = new EnumMap<>(World.Environment.class);
    public static boolean DIAMOND_ORE_ALERTS = false;

    static {
        POTION_LIMITS.put(PotionType.STRENGTH, 0);
        POTION_LIMITS.put(PotionType.WEAKNESS, 0);
        POTION_LIMITS.put(PotionType.SLOWNESS, 0);
        POTION_LIMITS.put(PotionType.INVISIBILITY, 0);
        POTION_LIMITS.put(PotionType.POISON, 0);

        ENCHANTMENT_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ENCHANTMENT_LIMITS.put(Enchantment.DAMAGE_ALL, 1);
        ENCHANTMENT_LIMITS.put(Enchantment.ARROW_KNOCKBACK, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.KNOCKBACK, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.FIRE_ASPECT, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.THORNS, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.ARROW_FIRE, 1);
        ENCHANTMENT_LIMITS.put(Enchantment.ARROW_DAMAGE, 4);

    }

    public static int SUBCLAIM_NAME_CHARACTERS_MIN = 3;
    public static int SUBCLAIM_NAME_CHARACTERS_MAX = 16;

    public static int FACTION_NAME_CHARACTERS_MIN = 3;
    public static int FACTION_NAME_CHARACTERS_MAX = 16;
    public static int MAX_MEMBERS_PER_FACTION = 25;

    public static int ROAD_DISTANCE = 2500;
    public static int ROAD_MIN_HEIGHT = 0; // 50 'this allowed people to claim below the roads, temp disabled;
    public static int ROAD_MAX_HEIGHT = 256; // 80 'this allowed people to claim above the roads, temp disabled;

    public static int END_PORTAL_RADIUS = 20;
    public static int END_PORTAL_CENTER = 500;

    public static long DEFAULT_DEATHBAN_DURATION = TimeUnit.HOURS.toMillis(1L);

    // Faction tag colours.
    public static ChatColor TEAMMATE_COLOUR;
    public static ChatColor ALLY_COLOUR;
    public static ChatColor ENEMY_COLOUR;

    public static ChatColor SAFEZONE_COLOUR;
    public static ChatColor ROAD_COLOUR;
    public static ChatColor WARZONE_COLOUR;
    public static ChatColor WILDERNESS_COLOUR;

    public static int MAX_ALLIES_PER_FACTION = 0;
    public static int MAX_CLAIMS_PER_FACTION = 8;
    public static boolean ALLOW_CLAIMING_BESIDES_ROADS = true;

    public static long DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45L);

    public static double DTR_INCREMENT_BETWEEN_UPDATES = 0.1;
    public static double MAXIMUM_DTR = 6.0;

    public static double EXP_MULTIPLIER_GENERAL = 2.0;
    public static double EXP_MULTIPLIER_FISHING = 2.0;
    public static double EXP_MULTIPLIER_SMELTING = 2.0;
    public static double EXP_MULTIPLIER_LOOTING_PER_LEVEL = 1.5;
    public static double EXP_MULTIPLIER_LUCK_PER_LEVEL = 1.5;
    public static double EXP_MULTIPLIER_FORTUNE_PER_LEVEL = 1.5;

    public static int CONQUEST_POINT_LOSS_PER_DEATH = 20;
    public static int CONQUEST_REQUIRED_WIN_POINTS = 300;

    public static boolean FOUND_DIAMONDS_ALERTS = true;
    public static long COMBAT_LOG_DESPAWN_TICKS = TimeUnit.SECONDS.toMillis(30L) / 50L;
    public static boolean COMBAT_LOG_PREVENTION_ENABLED = true;
}
