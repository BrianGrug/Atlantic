package me.scifi.hcf.deathban.lives.argument;

import com.doctordark.util.command.CommandArgument;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.scifi.hcf.ConfigurationService;
import me.scifi.hcf.HCF;
import me.scifi.hcf.faction.struct.Relation;
import me.scifi.hcf.faction.type.PlayerFaction;
import me.scifi.hcf.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import me.scifi.hcf.deathban.Deathban;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * An {@link CommandArgument} used to revive {@link Deathban}ned {@link Player}s.
 */
public class LivesReviveArgument extends CommandArgument {

    private static final String REVIVE_BYPASS_PERMISSION = "hcf.revive.bypass";
    private static final String PROXY_CHANNEL_NAME = "BungeeCord";

    private final HCF plugin;

    public LivesReviveArgument(HCF plugin) {
        super("revive", "Revive a death-banned player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + getName();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PROXY_CHANNEL_NAME);
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]); // TODO: breaking

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }

        UUID targetUUID = target.getUniqueId();
        FactionUser factionTarget = plugin.getManagerHandler().getUserManager().getUser(targetUUID);
        Deathban deathban = factionTarget.getDeathban();

        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }

        Relation relation = Relation.ENEMY;
        if (sender instanceof Player) {
            if (!sender.hasPermission(REVIVE_BYPASS_PERMISSION)) {
                if (ConfigurationService.KIT_MAP) {
                    sender.sendMessage(ChatColor.RED + "You cannot revive players during a kit map.");
                    return true;
                }

                if (plugin.getManagerHandler().getEotwHandler().isEndOfTheWorld()) {
                    sender.sendMessage(ChatColor.RED + "You cannot revive players during EOTW.");
                    return true;
                }
            }

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            int selfLives = plugin.getManagerHandler().getDeathbanManager().getLives(playerUUID);

            if (selfLives <= 0) {
                sender.sendMessage(ChatColor.RED + "You do not have any lives.");
                return true;
            }

            plugin.getManagerHandler().getDeathbanManager().setLives(playerUUID, selfLives - 1);
            PlayerFaction playerFaction = plugin.getManagerHandler().getFactionManager().getPlayerFaction(player);
            relation = playerFaction == null ? Relation.ENEMY : playerFaction.getFactionRelation(plugin.getManagerHandler().getFactionManager().getPlayerFaction(targetUUID));
            sender.sendMessage(ChatColor.YELLOW + "You have used a life to revive " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
        } else {
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ConfigurationService.ENEMY_COLOUR + target.getName() + ChatColor.YELLOW + '.');
        }

        if (sender instanceof PluginMessageRecipient) {
            // NOTE: This server needs at least 1 player online.
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(args[1]);

            String serverDisplayName = ChatColor.GREEN + "HCF"; // TODO: Non hard-coded server display name.
            out.writeUTF(relation.toChatColour() + sender.getName() + ChatColor.GOLD + " has just revived you from " + serverDisplayName + ChatColor.GOLD + '.');
            ((PluginMessageRecipient) sender).sendPluginMessage(plugin, PROXY_CHANNEL_NAME, out.toByteArray());
        }

        factionTarget.removeDeathban();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        Collection<FactionUser> factionUsers = plugin.getManagerHandler().getUserManager().getUsers().values();
        for (FactionUser factionUser : factionUsers) {
            Deathban deathban = factionUser.getDeathban();
            if (deathban == null || !deathban.isActive())
                continue;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
            String offlineName = offlinePlayer.getName();
            if (offlineName != null) {
                results.add(offlinePlayer.getName());
            }
        }

        return results;
    }
}
