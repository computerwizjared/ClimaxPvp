package net.climaxmc.KitPvp.Listeners;

import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.KitPvp;
import net.climaxmc.KitPvp.Utils.Duels.DuelsUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private ClimaxPvp plugin;

    public PlayerQuitListener(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (KitPvp.killStreak.containsKey(player.getUniqueId())) {
            KitPvp.killStreak.remove(player.getUniqueId());
        }

        DuelsUtils.removeDuel(player);

        event.setQuitMessage(null/*ChatColor.RED + "Quit" + ChatColor.DARK_GRAY + "\u00bb " + player.getName()*/);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.savePlayerData(plugin.getPlayerData(player)));
    }
}
