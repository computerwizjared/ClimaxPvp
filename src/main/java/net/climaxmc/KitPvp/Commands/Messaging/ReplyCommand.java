package net.climaxmc.KitPvp.Commands.Messaging;

import net.climaxmc.ClimaxPvp;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {
    private ClimaxPvp plugin;

    public ReplyCommand(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.getMessagers().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have not messaged anyone!");
            return true;
        }

        Player target = plugin.getServer().getPlayer(plugin.getMessagers().get(player.getUniqueId()));

        if (target == null) {
            player.sendMessage(ChatColor.RED + "The player that you previously messaged is no longer online.");
            return true;
        }

        plugin.getMessagers().put(player.getUniqueId(), target.getUniqueId());
        plugin.getMessagers().put(target.getUniqueId(), player.getUniqueId());

        String message = StringUtils.join(args, ' ', 0, args.length);

        player.sendMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.BOLD + " -> " + ChatColor.AQUA + "" + ChatColor.BOLD + target.getName() + ChatColor.AQUA + ": " + message.trim());
        target.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + player.getName() + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " -> " + ChatColor.DARK_AQUA + target.getName() + ChatColor.AQUA + ": " + message);

        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
        target.playSound(target.getLocation(), Sound.NOTE_PIANO, 2, 2);

        return true;
    }
}
