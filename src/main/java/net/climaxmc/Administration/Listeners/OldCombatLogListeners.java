package net.climaxmc.Administration.Listeners;

import net.climaxmc.Administration.Runnables.OldTagOff;
import net.climaxmc.ClimaxPvp;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

import java.util.*;

@Deprecated
public class OldCombatLogListeners implements Listener {
    private ClimaxPvp plugin;
    public static Set<UUID> tagged = new HashSet<>();
    public static Map<UUID, Long> tagTime = new HashMap<>();

    public OldCombatLogListeners(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (((event.getEntity() instanceof LivingEntity)) && (((event.getDamager() instanceof LivingEntity)) || ((event.getDamager() instanceof Projectile)))) {
            if ((event.getDamager() instanceof Player)) {
                Player player = (Player) event.getDamager();

                tagTime.put(player.getUniqueId(), System.currentTimeMillis());

                if (!tagged.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GRAY + "You are now in combat.");
                }

                int time = 15;

                if (!tagged.contains(player.getUniqueId())) {
                    new OldTagOff(plugin, player, time);
                }

                tagged.add(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (tagged.contains(player.getUniqueId())) {
            plugin.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + ChatColor.RED + " has logged out while in combat!");
            //TODO BAN THE PLAYER
            tagged.remove(player.getUniqueId());
            tagTime.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (tagged.contains(player.getUniqueId())) {
            tagged.remove(player.getUniqueId());
            tagTime.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (tagged.contains(player.getUniqueId()) && !(event.getMessage().toLowerCase().startsWith("/repair") || event.getMessage().toLowerCase().startsWith("/suicide"))) {
            player.sendMessage(ChatColor.RED + "You cannot run commands during combat!");
            event.setCancelled(true);
        }
    }
}
