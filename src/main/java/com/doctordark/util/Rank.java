package com.doctordark.util;

import lombok.AllArgsConstructor;
import me.activated.core.plugin.AquaCoreAPI;
import me.scifi.hcf.HCF;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Objects;

public class Rank {

    private HCF plugin = HCF.getPlugin();

    protected Permission permissions = null;

    protected Chat chat = null;

    public Rank(){
        if(plugin.getConfig().getString("PERMISSION-PLUGIN").equals("VAULT")){
            setupChat();
            setupPermissions();
        }
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permissions = rsp.getProvider();
        return permissions != null;
    }

    public String getGroupName(Player p){
        String group = "";
        switch (HCF.getPlugin().getConfig().getString("PERMISSION-PLUGIN")){
            case "VAULT": {
                group = permissions.getPrimaryGroup(p);
                break;
            }

            case "AQUACORE": {
                group = AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getName();
                break;
            }

            default: {
                group = "";
                break;
            }
        }
        return group;
    }

    public String getGroupPrefix(Player p){
        String prefix = "";
        switch (plugin.getConfig().getString("PERMISSION-PLUGIN")){
            case "VAULT" : {
                prefix = chat.getGroupPrefix(p.getWorld(), permissions.getPrimaryGroup(p));
                break;
            }

            case "AQUACORE" : {
                prefix = AquaCoreAPI.INSTANCE.getPlayerRank(p.getUniqueId()).getPrefix();
                break;
            }

            default: {
                prefix = "";
                break;
            }
        }
        return prefix;
    }


}
