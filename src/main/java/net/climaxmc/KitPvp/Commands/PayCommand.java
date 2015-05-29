package net.climaxmc.KitPvp.Commands;

import net.climaxmc.API.PlayerData;
import net.climaxmc.ClimaxPvp;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    private ClimaxPvp plugin;

    public PayCommand(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "/pay <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online!");
            return true;
        }

        int amount;

        try {
            amount = Math.abs(Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "/pay <player> <amount>");
            return true;
        }

        PlayerData playerData = plugin.getPlayerData(player);
        playerData.withdrawBalance(amount);
        player.sendMessage(ChatColor.GREEN + "You have sent " + target.getName() + " $" + amount + ".");

        PlayerData targetData = plugin.getPlayerData(target);
        targetData.depositBalance(amount);
        target.sendMessage(ChatColor.GREEN + "You have received $" + amount + " from " + player.getName() + ".");

        return true;
    }
}
