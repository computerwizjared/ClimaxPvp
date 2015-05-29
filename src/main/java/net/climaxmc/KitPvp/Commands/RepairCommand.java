package net.climaxmc.KitPvp.Commands;

import net.climaxmc.API.PlayerData;
import net.climaxmc.ClimaxPvp;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommand implements CommandExecutor {
    private ClimaxPvp plugin;

    public RepairCommand(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerData playerData = plugin.getPlayerData(player);
            if (playerData.getBalance() >= 2) {
                playerData.withdrawBalance(2);
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null) {
                        item.setDurability((short) -100);
                    }
                }
                for (ItemStack item : player.getInventory().getArmorContents()) {
                    if (item != null) {
                        item.setDurability((short) -100);
                    }
                }
                player.sendMessage("§aYou repaired your inventory for $2!");
            } else {
                player.sendMessage("§cYou do not have enough money to repair your inventory!");
            }
        }
        return true;
    }
}
