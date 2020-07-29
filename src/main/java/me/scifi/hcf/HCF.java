package me.scifi.hcf;

import com.doctordark.internal.com.doctordark.base.BasePlugin;
import com.doctordark.util.Config;
import com.doctordark.util.Rank;
import com.doctordark.util.itemdb.ItemDb;
import com.doctordark.util.itemdb.SimpleItemDb;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import me.scifi.hcf.abilities.command.AbilityCommand;
import me.scifi.hcf.abilities.listeners.BelchbombListener;
import me.scifi.hcf.abilities.listeners.GrapplingHookListener;
import me.scifi.hcf.abilities.listeners.SnowportListener;
import me.scifi.hcf.combatlog.CombatLogListener;
import me.scifi.hcf.command.*;
import me.scifi.hcf.customtimers.CustomTimerCommand;
import me.scifi.hcf.customtimers.CustomTimerManager;
import me.scifi.hcf.deathban.*;
import me.scifi.hcf.deathban.lives.LivesExecutor;
import me.scifi.hcf.economy.*;
import me.scifi.hcf.elevators.ElevatorListener;
import me.scifi.hcf.eventgame.CaptureZone;
import me.scifi.hcf.eventgame.EventExecutor;
import me.scifi.hcf.eventgame.EventScheduler;
import me.scifi.hcf.eventgame.conquest.ConquestExecutor;
import me.scifi.hcf.eventgame.eotw.EotwCommand;
import me.scifi.hcf.eventgame.eotw.EotwHandler;
import me.scifi.hcf.eventgame.eotw.EotwListener;
import me.scifi.hcf.eventgame.faction.CapturableFaction;
import me.scifi.hcf.eventgame.faction.ConquestFaction;
import me.scifi.hcf.eventgame.faction.KothFaction;
import me.scifi.hcf.eventgame.koth.KothExecutor;
import me.scifi.hcf.faction.FactionExecutor;
import me.scifi.hcf.faction.FactionMember;
import me.scifi.hcf.faction.FactionManager;
import me.scifi.hcf.faction.claim.*;
import me.scifi.hcf.faction.type.*;
import me.scifi.hcf.ktk.KTKCommand;
import me.scifi.hcf.ktk.KingListener;
import me.scifi.hcf.ktk.KingManager;
import me.scifi.hcf.listener.*;
import me.scifi.hcf.listener.fixes.*;
import me.scifi.hcf.managers.IManager;
import me.scifi.hcf.managers.ManagerHandler;
import me.scifi.hcf.pvpclass.PvpClassManager;
import me.scifi.hcf.pvpclass.bard.EffectRestorer;
import me.scifi.hcf.reclaims.ReclaimCommand;
import me.scifi.hcf.reclaims.ReclaimManager;
import me.scifi.hcf.reclaims.ResetReclaimCommand;
import me.scifi.hcf.scoreboard.Adapter;
import me.scifi.hcf.scoreboard.Assemble;
import me.scifi.hcf.scoreboard.AssembleStyle;
import me.scifi.hcf.signs.KitSignListener;
import me.scifi.hcf.sotw.SotwCommand;
import me.scifi.hcf.sotw.SotwListener;
import me.scifi.hcf.sotw.SotwTimer;
import me.scifi.hcf.staffmode.StaffModeCommand;
import me.scifi.hcf.staffmode.StaffModeListener;
import me.scifi.hcf.tablist.Azazel;
import me.scifi.hcf.tablist.AzazelAdapter;
import me.scifi.hcf.timer.TimerExecutor;
import me.scifi.hcf.timer.TimerManager;
import me.scifi.hcf.user.FactionUser;
import me.scifi.hcf.user.UserManager;
import me.scifi.hcf.visualise.ProtocolLibHook;
import me.scifi.hcf.visualise.VisualiseHandler;
import me.scifi.hcf.visualise.WallBorderListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class HCF extends JavaPlugin {

    @Getter
    private static HCF plugin;
    private Config messagesYML;
    private Config itemsYML = new Config(this, "items", getDataFolder().getAbsolutePath());
    private Config miscYML = new Config(this, "misc", getDataFolder().getAbsolutePath());
    private Config config;
    private Random random = new Random();
    private CombatLogListener combatLogListener;
    private EffectRestorer effectRestorer;
    private FoundDiamondsListener foundDiamondsListener;
    private SotwTimer sotwTimer;
    private ItemDb itemDb;
    private WorldEditPlugin worldEdit;
    private Cooldown lff = new Cooldown();
    private Cooldown lfa = new Cooldown();
    private Cooldown helpop = new Cooldown();
    private Cooldown rogue = new Cooldown();
    private Cooldown archerJumpBoost = new Cooldown();
    private Cooldown report = new Cooldown();
    private Rank rank;
    private ManagerHandler managerHandler;

    //Intellij shat itself so this is here
    public static Collection<? extends Player> getOnlinePlayers() {
        Collection<Player> collection = new ArrayList<>();
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            collection.add(player);
        }
        return collection;
    }

    public void onEnable() {
        plugin = this;
        config = new Config(this,"config", getDataFolder().getAbsolutePath());
        ProtocolLibHook.hook(this);
        BasePlugin.getPlugin().init(this);
        Plugin wep = getServer().getPluginManager().getPlugin("WorldEdit");
        worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin) wep : null;
        ConfigurationService.init(this);
        rank = new Rank();
        registerYMLS();
        registerConfiguration();
        itemDb = new SimpleItemDb(this);
        sotwTimer = new SotwTimer();
        this.managerHandler = new ManagerHandler();
        registerCommands();
        registerListeners();
        effectRestorer = new EffectRestorer(this);
        Assemble assemble = new Assemble(this, new Adapter());
        assemble.setTicks(2);
        assemble.setAssembleStyle(AssembleStyle.KOHI);
        new Azazel(this, new AzazelAdapter());

        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(plugin, TimeUnit.MINUTES.toMillis(20L), TimeUnit.MINUTES.toMillis(20L));
    }

    private void saveData() {
        this.managerHandler.getManagers().stream().forEach(IManager::unload);
    }

    @Override
    public void onDisable() {
        combatLogListener.removeCombatLoggers();
        BasePlugin.getPlugin().disable();
        managerHandler.getPvpClassManager().onDisable();
        foundDiamondsListener.saveConfig(); // temporary
        saveData();
        HCF.plugin = null; // always initialise last
    }

    private void registerYMLS() {
        messagesYML = new Config(this, "messages", getDataFolder().getAbsolutePath());
    }

    private void registerConfiguration() {
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(Deathban.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new BlockHitFixListener(), this);
        manager.registerEvents(new BlockJumpGlitchFixListener(), this);
        manager.registerEvents(new BoatGlitchFixListener(), this);
        manager.registerEvents(new BookDeenchantListener(), this);
        manager.registerEvents(new BorderListener(), this);
        manager.registerEvents(new BottledExpListener(), this);
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ClaimWandListener(this), this);
        manager.registerEvents(this.combatLogListener = new CombatLogListener(this), this);
        manager.registerEvents(new CoreListener(this), this);
        // manager.registerEvents(new CreativeClickListener(), this);
        manager.registerEvents(new CrowbarListener(this), this);
        manager.registerEvents(new DeathListener(this), this);
        manager.registerEvents(new DeathMessageListener(this), this);
        manager.registerEvents(new DeathSignListener(this), this);
        if (!getConfig().getBoolean("kit-map")) {
            manager.registerEvents(new DeathbanListener(this), this);
        }
        manager.registerEvents(new EnchantLimitListener(), this);
        manager.registerEvents(new EnderChestRemovalListener(), this);
        manager.registerEvents(new EntityLimitListener(), this);
        manager.registerEvents(new EotwListener(this), this);
        manager.registerEvents(new ExpMultiplierListener(), this);
        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents(this.foundDiamondsListener = new FoundDiamondsListener(this), this);
        manager.registerEvents(new FurnaceSmeltSpeederListener(), this);
        manager.registerEvents(new InfinityArrowFixListener(), this);
        manager.registerEvents(new PearlGlitchListener(this), this);
        manager.registerEvents(new PortalListener(this), this);
        manager.registerEvents(new PotionLimitListener(), this);
        manager.registerEvents(new ProtectionListener(this), this);
        manager.registerEvents(new SubclaimWandListener(this), this);
        manager.registerEvents(new SignSubclaimListener(this), this);
        manager.registerEvents(new ShopSignListener(this), this);
        manager.registerEvents(new SkullListener(), this);
        manager.registerEvents(new SotwListener(this), this);
        manager.registerEvents(new BeaconStrengthFixListener(), this);
        manager.registerEvents(new VoidGlitchFixListener(), this);
        manager.registerEvents(new WallBorderListener(this), this);
        manager.registerEvents(new WorldListener(this), this);
        manager.registerEvents(new KingListener(this), this);
        manager.registerEvents(new StaffModeListener(this), this);
        manager.registerEvents(new CobbleListener(this), this);
        manager.registerEvents(new FreezeListener(this), this);
        manager.registerEvents(new MuteChatListener(this), this);
        manager.registerEvents(new KitSignListener(this), this);
        manager.registerEvents(new ElevatorListener(), this);
        manager.registerEvents(new InspectionListener(),this);
        manager.registerEvents(new SnowportListener(),this);
        manager.registerEvents(new BelchbombListener(),this);
        manager.registerEvents(new GrapplingHookListener(),this);
        manager.registerEvents(new PunishListener(), this);
    }

    private void registerCommands() {
        getCommand("angle").setExecutor(new AngleCommand());
        getCommand("ktk").setExecutor(new KTKCommand(this));
        getCommand("conquest").setExecutor(new ConquestExecutor(this));
        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("eotw").setExecutor(new EotwCommand(this));
        getCommand("event").setExecutor(new EventExecutor(this));
        getCommand("faction").setExecutor(new FactionExecutor(this));
        getCommand("gopple").setExecutor(new GoppleCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this));
        getCommand("reclaim").setExecutor(new ReclaimCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("request").setExecutor(new RequestCommand(this));
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("cobble").setExecutor(new CobbleCommand(this));
        getCommand("koth").setExecutor(new KothExecutor(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("resetreclaim").setExecutor(new ResetReclaimCommand(this));
        getCommand("lives").setExecutor(new LivesExecutor(this));
        getCommand("reset").setExecutor(new ResetCommand(this));
        getCommand("location").setExecutor(new LocationCommand(this));
        getCommand("logout").setExecutor(new LogoutCommand(this));
        getCommand("flight").setExecutor(new FlightCommand(this));
        getCommand("set").setExecutor(new SetCommand(this));
        getCommand("help").setExecutor(new HelpCommand(this));
        getCommand("mapkit").setExecutor(new MapkitCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
        getCommand("servertime").setExecutor(new ServerTimeCommand());
        getCommand("sotw").setExecutor(new SotwCommand(this));
        getCommand("alert").setExecutor(new AlertCommand(this));
        getCommand("gmc").setExecutor(new GMCCommand(this));
        getCommand("gms").setExecutor(new GMSCommand(this));
        getCommand("spawncannon").setExecutor(new SpawnCannonCommand(this));
        getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
        getCommand("timer").setExecutor(new TimerExecutor(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("togglebroadcasts").setExecutor(new ToggleBroadcastsCommand());
        getCommand("togglecapzoneentry").setExecutor(new ToggleCapzoneEntryCommand(this));
        getCommand("togglelightning").setExecutor(new ToggleLightningCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("staffmode").setExecutor(new StaffModeCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("tl").setExecutor(new TLCommand(this));
        getCommand("setborder").setExecutor(new SetBorderCommand(this));
        getCommand("lff").setExecutor(new LFFCommand(this));
        getCommand("lfa").setExecutor(new LFACommand(this));
        getCommand("mutechat").setExecutor(new MuteChatCommand(this));
        getCommand("refund").setExecutor(new RefundCommand(this));
        getCommand("coordinates").setExecutor(new CoordinatesCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("teleport").setExecutor(new TeleportCommand(this));
        getCommand("tppos").setExecutor(new TPPosCommand(this));
        getCommand("gamemode").setExecutor(new GameModeCommand(this));
        getCommand("teleporthere").setExecutor(new TeleportHereCommand(this));
        getCommand("chest").setExecutor(new ChestCommand(this));
        getCommand("rename").setExecutor(new RenameCommand(this));
        getCommand("customtimer").setExecutor(new CustomTimerCommand(this));
        getCommand("top").setExecutor(new TopCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("ability").setExecutor(new AbilityCommand());
        Map<String, Map<String, Object>> map = getDescription().getCommands();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            PluginCommand command = getCommand(entry.getKey());
            command.setPermission("hcf.command." + entry.getKey());
            command.setPermissionMessage(ChatColor.RED + "No Permission.");
        }
    }


    private void registerManagers() {


    }

}
