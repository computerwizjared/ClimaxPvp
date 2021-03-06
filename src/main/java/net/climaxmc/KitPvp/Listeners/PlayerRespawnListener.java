package net.climaxmc.KitPvp.Listeners;

import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.Kit;
import net.climaxmc.KitPvp.KitManager;
import net.climaxmc.KitPvp.KitPvp;
import net.climaxmc.KitPvp.Utils.BlockUtils;
import net.climaxmc.KitPvp.Utils.ChatUtils;
import net.climaxmc.KitPvp.Utils.I;
import net.climaxmc.KitPvp.Utils.ServerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerRespawnListener implements Listener {
    private ClimaxPvp plugin;

    public PlayerRespawnListener(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == RegainReason.SATIATED)
            event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            if (plugin.getWarpLocation("Fair") != null) {
                if (player.getLocation().distance(plugin.getWarpLocation("Fair")) <= 50) {
                    event.setRespawnLocation(plugin.getWarpLocation("Fair"));
                }
            }
        }

        player.teleport(BlockUtils.getCenter(event.getRespawnLocation()));

        if (plugin.getScoreboard(player) == null) {
            ServerScoreboard serverScoreboard = new ServerScoreboard(player);
            serverScoreboard.updateScoreboard();
            plugin.scoreboards.put(player.getUniqueId(), serverScoreboard);
        }
        ServerScoreboard serverScoreboard = plugin.getScoreboard(player);
        serverScoreboard.updateScoreboard();

        for (Player players : Bukkit.getServer().getOnlinePlayers()){
            players.showPlayer(player);
        }

        KitPvp.getChecking().remove(player.getUniqueId());

        ClimaxPvp.deadPeoples.remove(player);

        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20F);
        player.setMaxHealth(20F);
        player.setFoodLevel(20);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFlySpeed(0.1F);
        player.setExp(0F);
        player.setLevel(0);

        player.setLevel(KitPvp.killStreak.get(player.getUniqueId()));

        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            plugin.showEntity(allPlayers, player);
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.updateInventory();
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, false, false));
        player.removePotionEffect(PotionEffectType.FAST_DIGGING);

        if (KitPvp.getVanished().contains(player.getUniqueId())) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        player.getInventory().setItem(0, new I(Material.NETHER_STAR)
                .name(ChatColor.GREEN + "Kit Selector")
                .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Select from a variety")
                .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "of different kits!"));

        if (KitManager.getPreviousKit().containsKey(player.getUniqueId())) {
            Kit kit = KitManager.getPreviousKit().get(player.getUniqueId());
            player.getInventory().setItem(1, new I(Material.REDSTONE)
                    .name(ChatColor.GREEN + "Previous Kit: " + kit.getColor() + kit.getName())
                    .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Equip your previous kit."));
        }

        /*Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (KitManager.isPlayerInKit(player)) {
                    return;
                }
                player.getInventory().setItem(3, new I(Material.POTION).durability(16421)
                        .name(ChatColor.translateAlternateColorCodes('&', "&8[&cAlpha&8] &fPractice"))
                        .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Indeed"));
            }
        }, 20L);*/

        player.getInventory().setItem(3, new I(Material.COMPASS)
                .name(ChatColor.DARK_PURPLE + "Warps")
                .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "View and teleport to various warps on the server!"));

        player.getInventory().setItem(5, new I(Material.PAPER)
                .name(ChatColor.AQUA + "Season II Info")
                .lore("View info about the current season!"));

        /*if (!plugin.spawnSoupTrue.containsKey(player)) {
            plugin.spawnSoupTrue.put(player, false);
        }
        if (ClimaxPvp.getInstance().spawnSoupTrue.get(player)) {
            player.getInventory().setItem(6, new I(Material.MUSHROOM_SOUP)
                    .name(ChatColor.GRAY + "Mode: " + ChatColor.YELLOW + "Soup")
                    .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Set your preferred healing type!"));
        } else {
            player.getInventory().setItem(6, new I(Material.FISHING_ROD)
                    .name(ChatColor.GRAY + "Mode: " + ChatColor.YELLOW + "Regen")
                    .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Set your preferred healing type!"));
        }*/

        player.getInventory().setItem(7, new I(Material.WATCH)
                .name(ChatColor.GOLD + "Settings")
                .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Customize your experience here at Climax!"));

        player.getInventory().setItem(8, new I(Material.BOOK)
                .name(ChatColor.AQUA + "Cosmetics")
                .lore(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Select from a variety of awesome cosmetics!"));

        /**
         * If they're at fair and the distance of the fair warp from the respawn location is less than 50 give them the duel stuff
         */
        if (plugin.getPlayersInWarp().containsKey(player.getUniqueId())) {
            String warp = plugin.getPlayersInWarp().get(player.getUniqueId()).toLowerCase();
            if (warp.equals("fair")) {
                if (event.getRespawnLocation().distance(plugin.getWarpLocation("fair")) <= 50) {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.getInventory().setItem(0, new I(Material.DIAMOND_SWORD).name(ChatUtils.color("&bPunch to duel")));
                }
            }
        }
        /*if (player.getLocation().distance(plugin.getWarpLocation("Duel")) <= 50) {
            player.getInventory().clear();
            player.getInventory().addItem(new I(Material.DIAMOND_AXE).name(org.bukkit.ChatColor.WHITE + "Duel Axe " + org.bukkit.ChatColor.AQUA + "(Punch a player!)"));
        }*/

        /*ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(ChatColor.GREEN + "Profile Menu");
        head.setItemMeta(meta);

        player.getInventory().setItem(2, head);*/
    }
}
