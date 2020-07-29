package me.scifi.hcf.managers;

import lombok.Getter;
import me.scifi.hcf.HCF;
import me.scifi.hcf.customtimers.CustomTimerManager;
import me.scifi.hcf.deathban.DeathbanManager;
import me.scifi.hcf.economy.EconomyManager;
import me.scifi.hcf.eventgame.EventScheduler;
import me.scifi.hcf.eventgame.eotw.EotwHandler;
import me.scifi.hcf.faction.FactionManager;
import me.scifi.hcf.faction.claim.ClaimHandler;
import me.scifi.hcf.ktk.KingManager;
import me.scifi.hcf.pvpclass.PvpClassManager;
import me.scifi.hcf.reclaims.ReclaimManager;
import me.scifi.hcf.timer.TimerManager;
import me.scifi.hcf.user.UserManager;
import me.scifi.hcf.visualise.VisualiseHandler;

import java.util.Arrays;
import java.util.List;

@Getter
public class ManagerHandler {

    private final List<IManager> managers;

    private final HCF plugin = HCF.getPlugin();

    private final KingManager kingManager;

    private final ReclaimManager reclaimManager;

    private final FactionManager factionManager;

    private final PvpClassManager pvpClassManager;

    private final CustomTimerManager customTimerManager;

    private final TimerManager timerManager;

    private final EconomyManager economyManager;

    private final DeathbanManager deathbanManager;

    private final UserManager userManager;

    private final VisualiseHandler visualiseHandler;

    private final EotwHandler eotwHandler;

    private final ClaimHandler claimHandler;

    private final EventScheduler eventScheduler;

    public ManagerHandler() {
        this.managers = Arrays.asList(new KingManager(),
                new ReclaimManager(),
                new FactionManager(plugin),
                new PvpClassManager(plugin),
                new CustomTimerManager(),
                new TimerManager(plugin),
                new EconomyManager(plugin),
                new DeathbanManager(plugin),
                new UserManager(plugin),
                new VisualiseHandler(),
                new EotwHandler(plugin),
                new EventScheduler(plugin),
                new ClaimHandler(plugin));
        this.managers.forEach(IManager::load);

        this.kingManager = findHandler(KingManager.class);
        this.reclaimManager = findHandler(ReclaimManager.class);
        this.factionManager = findHandler(FactionManager.class);
        this.pvpClassManager = findHandler(PvpClassManager.class);
        this.customTimerManager = findHandler(CustomTimerManager.class);
        this.timerManager = findHandler(TimerManager.class);
        this.economyManager = findHandler(EconomyManager.class);
        this.deathbanManager = findHandler(DeathbanManager.class);
        this.userManager = findHandler(UserManager.class);
        this.visualiseHandler = findHandler(VisualiseHandler.class);
        this.eotwHandler = findHandler(EotwHandler.class);
        this.eventScheduler = findHandler(EventScheduler.class);
        this.claimHandler = findHandler(ClaimHandler.class);
    }

    public <T> T findHandler(Class<T> clazz) {
        return (T) this.managers.stream().filter(iManager -> iManager.getClass().equals(clazz)).findFirst().orElse(null);
    }

}
