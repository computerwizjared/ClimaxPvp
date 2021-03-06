package net.climaxmc.KitPvp.Listeners;

import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.Commands.ReportCommand;
import net.climaxmc.KitPvp.KitPvp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

        if (ClimaxPvp.inDuel.contains(player)) {
            if (ClimaxPvp.isDueling.containsKey(player)) {
                Player opponent = ClimaxPvp.isDueling.get(player);
                player.sendMessage(ChatColor.WHITE + "\u00BB " + ChatColor.YELLOW + "Your opponent has left the game! Winner: " + ChatColor.GOLD + opponent.getDisplayName());
                opponent.sendMessage(ChatColor.WHITE + "\u00BB " + ChatColor.YELLOW + "Your opponent has left the game! Winner: " + ChatColor.GOLD + opponent.getDisplayName());

                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        plugin.respawn(player);
                    }
                }, 20L * 3);
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        plugin.respawn(opponent);
                    }
                }, 20L * 3);

                ClimaxPvp.inDuel.remove(player);
                ClimaxPvp.inDuel.remove(opponent);
            } else {
                Player opponent = ClimaxPvp.isDuelingReverse.get(player);
                player.sendMessage(ChatColor.WHITE + "\u00BB " + ChatColor.YELLOW + "Your opponent has left the game! Winner: " + ChatColor.GOLD + opponent.getDisplayName());
                opponent.sendMessage(ChatColor.WHITE + "\u00BB " + ChatColor.YELLOW + "Your opponent has left the game! Winner: " + ChatColor.GOLD + opponent.getDisplayName());

                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        plugin.respawn(player);
                    }
                }, 20L * 3);
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        plugin.respawn(opponent);
                    }
                }, 20L * 3);

                ClimaxPvp.inDuel.remove(player);
                ClimaxPvp.inDuel.remove(opponent);
            }
        }

        if (ReportCommand.getReportBuilders().containsKey(player.getUniqueId())) {
            ReportCommand.getReportBuilders().remove(player.getUniqueId());
        }

        if (ReportCommand.getReportArray().containsKey(player.getUniqueId())) {
            ReportCommand.getReportArray().remove(player.getUniqueId());
        }

        if (plugin.getServer().getOnlinePlayers().size() < 7) {
            plugin.getServer().getOnlinePlayers().stream().filter(players -> KitPvp.currentTeams.containsKey(players.getName())
                    || KitPvp.currentTeams.containsValue(players.getName())).forEach(players -> {
                players.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                players.sendMessage(ChatColor.RED + "Since there are less than 7 players online, your team has been suspended!");
                players.sendMessage(ChatColor.GRAY + "It will resume once there are 7 players online again.");
            });
        }

        /*if (KitPvp.currentTeams.containsKey(player.getName())) {
            Player teammate = Bukkit.getServer().getPlayer(KitPvp.currentTeams.get(player.getName()));
            teammate.playSound(teammate.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1F);
            teammate.sendMessage(ChatColor.RED + " " + player.getName() + " has left the server. Therefore, the team has been disbanded!");
            KitPvp.currentTeams.remove(player.getName());
        } else if (KitPvp.currentTeams.values().contains(player.getName())) {
            int i = 0;
            for (String key : KitPvp.currentTeams.keySet()) {
                i++;
                if (KitPvp.currentTeams.get(key).equalsIgnoreCase(player.getName())) {
                    Player teammate = Bukkit.getServer().getPlayer(KitPvp.currentTeams.get(key));
                    teammate.playSound(teammate.getLocation(), Sound.CHEST_CLOSE, 0.5F, 1F);
                    teammate.sendMessage(ChatColor.RED + " " + player.getName() + " has left the server. Therefore, the team has been disbanded!");
                    KitPvp.currentTeams.remove(KitPvp.currentTeams.get(key));
                }
            }
        }*/

        event.setQuitMessage(null/*ChatColor.RED + "Quit" + ChatColor.DARK_GRAY + "\u00bb " + player.getName()*/);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.savePlayerData(plugin.getPlayerData(player)));

        plugin.scoreboards.remove(player.getUniqueId());
    }

}
