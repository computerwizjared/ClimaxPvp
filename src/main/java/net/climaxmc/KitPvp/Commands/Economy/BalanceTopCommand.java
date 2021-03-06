package net.climaxmc.KitPvp.Commands.Economy;

import net.climaxmc.ClimaxPvp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceTopCommand implements CommandExecutor {
    private ClimaxPvp plugin;

    public BalanceTopCommand(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        ResultSet set = plugin.getMySQL().executeQuery("SELECT *, SUM(balance) AS total_balance FROM `climax_playerdata` GROUP BY `uuid` ORDER BY total_balance DESC LIMIT 10");
        Map<UUID, Integer> topBalances = new LinkedHashMap<>();

        try {
            while (set.next()) {
                topBalances.put(UUID.fromString(set.getString("uuid")), set.getInt("balance"));
            }
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Error getting top balances!");
        }

        player.sendMessage(" \u00A7f[\u00A77=\u00A78\u00A7m---\u00A77=\u00A7f] " + ChatColor.AQUA + "Top 10 Balances" + " \u00A7f[\u00A77=\u00A78\u00A7m---\u00A77=\u00A7f]");

        int i = 0;

        for (UUID uuid : topBalances.keySet()) {
            int balance = topBalances.get(uuid);
            if (balance != 0) {
                player.sendMessage(ChatColor.GRAY + " " + ++i + ". " + ChatColor.GREEN + plugin.getServer().getOfflinePlayer(uuid).getName()
                        + ChatColor.WHITE + " - " + ChatColor.GREEN + "$" + balance);
            }
        }

        player.sendMessage(" \u00A7f[\u00A77=\u00A78\u00A7m--------\u00A77=\u00A7f]\u00A7f\u00A7m----\u00A7f[\u00A77=\u00A78\u00A7m---------\u00A77=\u00A7f]");

        return true;
    }
}
