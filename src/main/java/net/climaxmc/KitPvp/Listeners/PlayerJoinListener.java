package net.climaxmc.KitPvp.Listeners;

import net.climaxmc.Administration.Commands.ChatCommands;
import net.climaxmc.Administration.Commands.FreezeCommand;
import net.climaxmc.Administration.Punishments.Punishment;
import net.climaxmc.Administration.Punishments.Time;
import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.KitPvp;
import net.climaxmc.KitPvp.Utils.DeathEffects.DeathEffect;
import net.climaxmc.KitPvp.Utils.DeathEffects.DeathEffectFiles;
import net.climaxmc.KitPvp.Utils.ServerScoreboard;
import net.climaxmc.KitPvp.Utils.Settings.SettingsFiles;
import net.climaxmc.KitPvp.Utils.TextComponentMessages;
import net.climaxmc.KitPvp.Utils.Titles.TitleFiles;
import net.climaxmc.common.database.PlayerData;
import net.climaxmc.common.database.Rank;
import net.climaxmc.common.donations.trails.Trail;
import net.climaxmc.common.titles.Title;
import org.bukkit.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerJoinListener implements Listener {
    private ClimaxPvp plugin;

    private File file;
    private FileConfiguration config;

    public PlayerJoinListener(ClimaxPvp plugin) {
        this.file = new File(ClimaxPvp.getInstance().getDataFolder() + File.separator + "settings.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        this.plugin = plugin;
    }

    private void set(String path, Object object) {
        config.set(path, object);
    }

    @EventHandler
    public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent event) {

        plugin.getMySQL().createPlayerData(event.getUniqueId(), event.getAddress().getHostAddress());

        List<Punishment> punishments = new ArrayList<>();
        PlayerData playerData = plugin.getPlayerData(plugin.getServer().getOfflinePlayer(event.getUniqueId()));

        if (playerData != null) {
            if (playerData.hasRank(Rank.OWNER)) {
                return;
            }

            punishments.addAll(playerData.getPunishments());
        }

        punishments.addAll(plugin.getMySQL().getPunishmentsFromIP(event.getAddress().getHostAddress()));

        punishments.stream().filter(punishment -> punishment.getType().equals(Punishment.PunishType.BAN)).forEach( punishment -> {
            if (System.currentTimeMillis() <= (punishment.getTime() + punishment.getExpiration())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You were temporarily banned by " + plugin.getServer().getOfflinePlayer(punishment.getPunisherUUID()).getName()
                        + " for " + punishment.getReason() + ".\n"
                        + "You have " + Time.toString(punishment.getTime() + punishment.getExpiration() - System.currentTimeMillis()) + " left in your ban.\n"
                        + "Appeal on forum.climaxmc.net if you believe that this is in error!");
            } else if (punishment.getExpiration() == -1) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You were permanently banned by " + plugin.getServer().getOfflinePlayer(punishment.getPunisherUUID()).getName()
                        + " for " + punishment.getReason() + ".\n"
                        + "Appeal on forum.climaxmc.net if you believe that this is in error!");
            }
        });

        if (playerData != null) {
            if (playerData.hasRank(Rank.TRUSTED)) {
                return;
            }
        }

        /**
         * disabled cus hub
         */
        /*if (plugin.getConfig().getLong("IPQueriesTime") < System.currentTimeMillis() || plugin.getConfig().getLong("IPQueriesTime") == 0) {
            plugin.getConfig().set("IPQueries", 0);
            plugin.getConfig().set("IPQueriesTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
            plugin.saveConfig();
        }

        if (plugin.getConfig().getInt("IPQueries") < 450) {
            plugin.getConfig().set("IPQueries", plugin.getConfig().getInt("IPQueries") + 1);
            plugin.saveConfig();
            try {
                URL url = new URL("http://check.getipintel.net/check.php?ip=" + event.getAddress().getHostAddress() + "&contact=computerwizjared@hotmail.com&flags=m");
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                double result = Double.valueOf(in.readLine());
                if (result == 1) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "\nSorry, but we don't allow VPNs or Proxies on Climax.\n" +
                            "Please disable your VPN or Proxy and retry!");
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerData(player);

        TitleFiles titleFiles = new TitleFiles();
        if (titleFiles.getCurrentTitle(player) != null) {
            for (Title title : Title.values()) {
                if (title.getName().equals(titleFiles.getCurrentTitle(player))) {
                    ClimaxPvp.inTitle.put(player, title.getTitle());
                    titleFiles.setCurrentTitle(player, title);
                }
            }
        }

        event.setJoinMessage((player.hasPlayedBefore() ? ChatColor.DARK_AQUA : ChatColor.GOLD) + "Join" + ChatColor.DARK_GRAY + "\u00BB " + player.getName());

        plugin.respawn(player);

        plugin.getMySQL().getTemporaryPlayerData().put(player.getUniqueId(), new HashMap<>());

        if (playerData != null) {
            if (!playerData.getIp().equals(player.getAddress().getAddress().getHostAddress()) && !playerData.getIp().equals("0.0.0.0")) {
                playerData.setIP(player.getAddress().getAddress().getHostAddress());
            }

            player.setDisplayName(playerData.getNickname());
            String rankTag = "";

            if (playerData.hasRank(Rank.NINJA)) {
                rankTag = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + playerData.getRank().getColor()
                        + ChatColor.BOLD + playerData.getRank().getPrefix() + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] ";
                player.setPlayerListName(/*rankTag + */playerData.getLevelColor() + playerData.getRank().getColor() + ChatColor.BOLD + player.getName());
            }
            if (playerData.getRank().getColor() != null) {
                player.setPlayerListName(playerData.getLevelColor() + playerData.getRank().getColor() + player.getName());
            } else {
                player.setPlayerListName(playerData.getLevelColor() + player.getName());
            }

            if (playerData.hasRank(Rank.OWNER)) {
                if (!player.isOp()) {
                    player.setOp(true);
                    player.sendMessage(ChatColor.BOLD + "You were opped because you had been previously deopped.");
                }
            }
        } else {
            player.setPlayerListName(ChatColor.GRAY + player.getName());
        }

        //Commenting this out in attempts to make joining less laggy
        /*int playersOnline = plugin.getServer().getOnlinePlayers().size();

        if (playersOnline > plugin.getConfig().getInt("HighestPlayerCount")) {
            plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "We have reached a new high player count of " + playersOnline + "!");
            plugin.getConfig().set("HighestPlayerCount", playersOnline);
            plugin.saveConfig();
        }*/

        if (ChatCommands.cmdspies.contains(player.getUniqueId()) && !playerData.hasRank(Rank.TRIAL_MODERATOR)) {
            ChatCommands.cmdspies.remove(player.getUniqueId());
        }

        if (playerData.hasRank(Rank.TRIAL_MODERATOR)) {
            ChatCommands.cmdspies.add(player.getUniqueId());
        }

        player.setWalkSpeed(0.2F);

        PermissionAttachment attachment = player.addAttachment(plugin);

        attachment.setPermission("bukkit.command.tps", true);
        attachment.setPermission("noattackcooldown.use", true);
        attachment.setPermission("pp.use", true);
        attachment.setPermission("poll.command.vote", true);
        attachment.setPermission("portals.use", true);
        attachment.setPermission("AAC.bypass", false);
        attachment.setPermission("worldedit.calc", false);
        if (!player.isOp()) {
            attachment.setPermission("bukkit.command.plugins", false);
            attachment.setPermission("bukkit.command.version", false);
        }

        if (playerData != null) {
            if (playerData.hasRank(Rank.TRIAL_MODERATOR)) {

                attachment.setPermission("antinub.staff", true);

                attachment.setPermission("litebans.ban", true);
                attachment.setPermission("litebans.unlimited", true);
                attachment.setPermission("litebans.banlist", true);
                attachment.setPermission("litebans.checkban", true);
                attachment.setPermission("litebans.clearchat", true);
                attachment.setPermission("litebans.history", true);
                attachment.setPermission("litebans.kick", true);
                attachment.setPermission("litebans.mute", true);
                attachment.setPermission("litebans.notify", true);
                attachment.setPermission("litebans.override", true);
                attachment.setPermission("litebans.tempmute", true);
                attachment.setPermission("litebans.tempban", true);
                attachment.setPermission("litebans.togglechat", true);
                attachment.setPermission("litebans.unmute", true);
                attachment.setPermission("litebans.unwarn", true);
                attachment.setPermission("litebans.warn", true);
                attachment.setPermission("litebans.warnings", true);
                attachment.setPermission("litebans.notify.dupeip_join", false);
            }

            if (playerData.hasRank(Rank.MODERATOR)) {
                attachment.setPermission("minecraft.command.tp", true);
                attachment.setPermission("bukkit.command.teleport", true);
                attachment.setPermission("litebans.ban", true);
                attachment.setPermission("litebans.dupeip", true);
                attachment.setPermission("litebans.ipban", true);
                attachment.setPermission("litebans.unlimited", true);
                attachment.setPermission("litebans.notify.dupeip_join", true);
                attachment.setPermission("litebans.unban", true);
            }

            if (playerData.hasRank(Rank.SENIOR_MODERATOR)) {
                attachment.setPermission("libsdisguises.disguise.*", true);
            }

            if (playerData.hasRank(Rank.ADMINISTRATOR)) {
                attachment.setPermission("phoenix.command", true);
            }
        }

        String joinMOTD = plugin.getConfig().getString("JoinMOTD");
        if (joinMOTD != null) {
            player.sendMessage("" + ChatColor.translateAlternateColorCodes('&', joinMOTD));
        }
    }

    @EventHandler
    public void scoreboardJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ServerScoreboard serverScoreboard = new ServerScoreboard(player);
        serverScoreboard.updateScoreboard();
        plugin.scoreboards.put(player.getUniqueId(), serverScoreboard);
    }

    @EventHandler
    public void settingsJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Settings global chat hashmap update
        SettingsFiles settingsFiles = new SettingsFiles();
        Bukkit.getScheduler().runTaskAsynchronously(ClimaxPvp.getInstance(), () -> {
            if (!settingsFiles.getGlobalChatValue(player)) {
                KitPvp.globalChatDisabled.add(player.getUniqueId());
            }

            if (config.get(player.getUniqueId() + ".receiveMsging") == null) {
                set(player.getUniqueId() + ".receiveMsging", true);
                settingsFiles.saveConfig();
            }
            if (config.get(player.getUniqueId() + ".globalChat") == null) {
                set(player.getUniqueId() + ".globalChat", true);
                settingsFiles.saveConfig();
            }
            settingsFiles.saveConfig();
        });
    }
}
