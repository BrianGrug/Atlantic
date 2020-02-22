package com.doctordark.internal.com.doctordark.base;

import com.doctordark.util.chat.HttpMojangLang;
import com.doctordark.util.chat.Lang;
import com.doctordark.util.PersistableLocation;
import com.doctordark.util.SignHandler;
import com.doctordark.util.chat.MojangLang;
import com.doctordark.util.itemdb.SimpleItemDb;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import com.doctordark.util.cuboid.Cuboid;
import com.doctordark.util.cuboid.NamedCuboid;
import com.doctordark.util.itemdb.ItemDb;

import java.io.IOException;
import java.util.Random;

public class BasePlugin  {

    @Getter
    private static BasePlugin plugin;

    @Getter
    private Random random = new Random();

    @Getter
    private ItemDb itemDb;

    @Getter
    private SignHandler signHandler;

    @Getter
    private MojangLang language;

    private JavaPlugin jp;

    private void registerManagers() {
        this.language = new HttpMojangLang();
        try {
            Lang.initialize("en_US");
            //this.language.index("1.7.10", Locale.ENGLISH);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void init(JavaPlugin plugin) {
        this.jp = plugin;
        ConfigurationSerialization.registerClass(PersistableLocation.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        ConfigurationSerialization.registerClass(NamedCuboid.class);
        this.itemDb = new SimpleItemDb(plugin);
        this.signHandler = new SignHandler(plugin);
    }


    public void disable() {
        this.signHandler.cancelTasks(null);
        this.jp = null;
        BasePlugin.plugin = null;
    }

    public static BasePlugin getPlugin(){
        return BasePlugin.plugin;
    }

    public JavaPlugin getJavaPlugin(){
        return jp;
    }

    static {
        BasePlugin.plugin = new BasePlugin();
    }
}
