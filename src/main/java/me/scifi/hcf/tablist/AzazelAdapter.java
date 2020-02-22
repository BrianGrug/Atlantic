package me.scifi.hcf.tablist;

import com.doctordark.util.Config;
import me.scifi.hcf.HCF;
import me.scifi.hcf.Utils;
import me.scifi.hcf.tablist.tab.TabAdapter;
import me.scifi.hcf.tablist.tab.TabTemplate;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class AzazelAdapter implements TabAdapter {
    private Config tabconfig = new Config(HCF.getPlugin(),"tab",HCF.getPlugin().getDataFolder().getAbsolutePath());
    @Override
    public TabTemplate getTemplate(Player player) {
        TabTemplate template = new TabTemplate();
        template.middle(0, Utils.chat(tabconfig.getString("SERVER-NAME")));
        template.middle(1,Utils.chat(tabconfig.getString("SERVER-WEBSITE")));
        template.left(0, Utils.chat(tabconfig.getString("PLAYER-INFO")));
        template.left(1, Utils.chat(tabconfig.getString("PLAYER-INFO-KILLS").replace("%kills%", Integer.toString(player.getStatistic(Statistic.PLAYER_KILLS)))));
        template.left(2, Utils.chat(tabconfig.getString("PLAYER-INFO-DEATHS").replace("%deaths%", Integer.toString(player.getStatistic(Statistic.DEATHS)))));
        template.left(4, Utils.chat(tabconfig.getString("MAP-KIT")));
        template.left(5, Utils.chat(tabconfig.getString("MAP-KIT-ENCHANTS")));
        template.right(0, Utils.chat(tabconfig.getString("END-PORTAL")));
        template.right(1, Utils.chat(tabconfig.getString("END-PORTAL-COORDNATES")));
        template.right(2, Utils.chat(tabconfig.getString("END-PORTAL-QUADRENTS")));
        template.right(4, Utils.chat(tabconfig.getString("WORLD-BORDER")));
        template.right(5, Utils.chat(tabconfig.getString("WORLD-BORDER-SIZE")));

        return template;
    }
}
