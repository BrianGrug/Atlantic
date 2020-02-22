package me.scifi.hcf.user;

import com.doctordark.compat.com.google.common.collect.GuavaCompat;
import com.doctordark.util.Config;
import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.scifi.hcf.HCF;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserManager implements Listener {

    private final HCF plugin;

    private final Map<UUID, FactionUser> users = new HashMap<>();
    private Config userConfig;

    public UserManager(HCF plugin) {
        this.plugin = plugin;
        this.reloadUserData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        users.putIfAbsent(uuid, new FactionUser(uuid));
    }

    /**
     * Gets a map of {@link FactionUser} this manager holds.
     *
     * @return map of user UUID strings to their corresponding {@link FactionUser}.
     */
    public Map<UUID, FactionUser> getUsers() {
        return users;
    }

    /**
     * Gets a {@link FactionUser} by their {@link UUID} asynchronously.
     *
     * @param uuid
     *            the {@link UUID} to get from
     * @return the {@link FactionUser} with the {@link UUID}
     */
    public FactionUser getUserAsync(UUID uuid) {
        synchronized (users) {
            FactionUser revert;
            FactionUser user = users.putIfAbsent(uuid, revert = new FactionUser(uuid));
            return GuavaCompat.firstNonNull(user, revert);
        }
    }

    /**
     * Gets a {@link FactionUser} by their {@link UUID}.
     *
     * @param uuid
     *            the {@link UUID} to get from
     * @return the {@link FactionUser} with the {@link UUID}
     */
    public FactionUser getUser(UUID uuid) {
        FactionUser revert;
        FactionUser user = users.putIfAbsent(uuid, revert = new FactionUser(uuid));
        return GuavaCompat.firstNonNull(user, revert);
    }

    /**
     * Loads the user data from storage.
     */
    public void reloadUserData() {
        this.userConfig = new Config(plugin, "faction-users", plugin.getDataFolder().getAbsolutePath());

        Object object = userConfig.get("users");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            Collection<String> keys = section.getKeys(false);
            for (String id : keys) {
                users.put(UUID.fromString(id), (FactionUser) userConfig.get(section.getCurrentPath() + '.' + id));
            }
        }
    }

    /**
     * Saves the user data to storage.
     */
    public void saveUserData() {
        Set<Map.Entry<UUID, FactionUser>> entrySet = users.entrySet();
        Map<String, FactionUser> saveMap = new LinkedHashMap<>(entrySet.size());
        for (Map.Entry<UUID, FactionUser> entry : entrySet) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }

        userConfig.set("users", saveMap);
        userConfig.save();
    }
}
