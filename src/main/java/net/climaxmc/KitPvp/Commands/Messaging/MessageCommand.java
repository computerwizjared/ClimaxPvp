package net.climaxmc.KitPvp.Commands.Messaging;

import net.climaxmc.Administration.Punishments.Punishment;
import net.climaxmc.Administration.Punishments.Time;
import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.KitPvp;
import net.climaxmc.KitPvp.Utils.Settings.SettingsFiles;
import net.climaxmc.common.database.PlayerData;
import net.climaxmc.common.database.Rank;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class MessageCommand implements CommandExecutor {
    private ClimaxPvp plugin;

    public MessageCommand(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = plugin.getPlayerData(player);

        for (Punishment punishment : playerData.getPunishments().stream().filter(punishment1 -> punishment1.getType().equals(Punishment.PunishType.MUTE)).collect(Collectors.toSet())) {
            PlayerData punisherData = plugin.getPlayerData(plugin.getServer().getOfflinePlayer(punishment.getPunisherUUID()));
            if (System.currentTimeMillis() <= (punishment.getTime() + punishment.getExpiration())) {
                player.sendMessage(ChatColor.RED + "You were temporarily muted by " + plugin.getServer().getOfflinePlayer(punisherData.getUuid()).getName()
                        + " for " + punishment.getReason() + ".\n"
                        + "You have " + Time.toString(punishment.getTime() + punishment.getExpiration() - System.currentTimeMillis()) + " left in your mute.\n"
                        + "Appeal on forum.climaxmc.net if you believe that this is in error!");
                return true;
            } else if (punishment.getExpiration() == -1) {
                player.sendMessage(ChatColor.RED + "You were permanently muted by " + plugin.getServer().getOfflinePlayer(punisherData.getUuid()).getName()
                        + " for " + punishment.getReason() + ".\n"
                        + "Appeal on forum.climaxmc.net if you believe that this is in error!");
                return true;
            }
        }

        if (args.length <= 1) {
            player.sendMessage(ChatColor.RED + "/" + label + " <player> <message>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null || KitPvp.getVanished().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "That player is not online!");
            return true;
        }

        SettingsFiles settingsFiles = new SettingsFiles();
        if (settingsFiles.getReceiveMsgValue(target) == false && !playerData.hasRank(Rank.TRIAL_MODERATOR)) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR! " + ChatColor.WHITE + "\u00BB " + ChatColor.YELLOW + target.getName() + ChatColor.RED + " has receiving msgs disabled!");
            return true;
        }

        plugin.getMessagers().put(player.getUniqueId(), target.getUniqueId());
        plugin.getMessagers().put(target.getUniqueId(), player.getUniqueId());

        String message = StringUtils.join(args, ' ', 1, args.length);

        if (KitPvp.getAfk().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.AQUA + target.getName() + " is AFK, so they might not see your message");
        }

        player.sendMessage(ChatColor.DARK_AQUA + "You" + ChatColor.RED + " \u00BB " + ChatColor.AQUA + "" + ChatColor.BOLD + target.getName() + ChatColor.WHITE + ": " + ChatColor.AQUA + message.trim());
        target.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + player.getName() + ChatColor.DARK_AQUA + "" + ChatColor.RED + " \u00BB " + ChatColor.DARK_AQUA + "You" + ChatColor.WHITE + ": " + ChatColor.AQUA + message);

        /*if (target.getUniqueId().toString().equals("99fa296e-7397-40bd-abbe-e4ca50b1427c")) {
            player.sendMessage(ChatColor.GOLD + "Jared is often AFK due to plugin development. Please be patient for a reply.");
        } else if (target.getUniqueId().toString().equals("66ca47bf-14ae-405b-9ff5-ef4bb98035eb")) {
            player.sendMessage(ChatColor.RED + "Gamer is often AFK due to plugin development. If he's AFK, he'll get back to you when he can!");
        }*/

        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 2, 2);
        target.playSound(target.getLocation(), Sound.NOTE_PIANO, 2, 2);

        return true;
    }
}
