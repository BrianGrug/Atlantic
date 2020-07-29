package me.scifi.hcf.economy;

import com.doctordark.util.Config;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;
import me.scifi.hcf.managers.IManager;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the {@link EconomyManager} storing to YAML.
 */
public class EconomyManager implements IManager {

    private final JavaPlugin plugin;

    public static final char ECONOMY_SYMBOL = '$';

    private TObjectIntMap<UUID> balanceMap = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);
    private Config balanceConfig;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public TObjectIntMap<UUID> getBalanceMap() {
        return this.balanceMap;
    }

    public int getBalance(UUID uuid) {
        return this.balanceMap.get(uuid);
    }

    public int setBalance(UUID uuid, int amount) {
        this.balanceMap.put(uuid, amount);
        return amount;
    }

    public int addBalance(UUID uuid, int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) + amount);
    }

    public int subtractBalance(UUID uuid, int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) - amount);
    }

    public void load() {
        Object object = (this.balanceConfig = new Config(plugin, "balances",plugin.getDataFolder().getAbsolutePath())).get("balances");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            Set<String> keys = section.getKeys(false);
            for (String id : keys) {
                this.balanceMap.put(UUID.fromString(id), this.balanceConfig.getInt("balances." + id));
            }
        }
    }

    @Override
    public void unload() {
        Map<String, Integer> saveMap = new LinkedHashMap<>(this.balanceMap.size());
        this.balanceMap.forEachEntry(new TObjectIntProcedure<UUID>() {
            @Override
            public boolean execute(UUID uuid, int i) {
                saveMap.put(uuid.toString(), i);
                return true;
            }
        });

        this.balanceConfig.set("balances", saveMap);
        this.balanceConfig.save();
    }
}
