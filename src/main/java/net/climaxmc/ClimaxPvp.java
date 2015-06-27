package net.climaxmc;

import lombok.Getter;
import net.climaxmc.Administration.Administration;
import net.climaxmc.Administration.Runnables.UpdateRunnable;
import net.climaxmc.Donations.Donations;
import net.climaxmc.KitPvp.KitPvp;
import net.climaxmc.KitPvp.Kits.PvpKit;
import net.climaxmc.common.database.MySQL;
import net.climaxmc.common.database.PlayerData;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;

public class ClimaxPvp extends JavaPlugin {
    @Getter
    private static ClimaxPvp instance = null;
    @Getter
    public HashMap<UUID, Location> currentWarps = new HashMap<>();
    @Getter
    private MySQL mySQL = null;
    @Getter
    private String prefix = ChatColor.BLACK + "" + ChatColor.BOLD + "[" + ChatColor.RED + "Climax" + ChatColor.BLACK + "" + ChatColor.BOLD + "] " + ChatColor.RESET;
    @Getter
    private List<String> rules = new ArrayList<>();
    @Getter
    private List<String> help = new ArrayList<>();
    @Getter
    private String book = "";
    // Warps Configuration
    @Getter
    private FileConfiguration warpsConfig = null;
    private File warpsConfigFile = null;

    @Override
    public void onEnable() {
        // Initialize Instance
        instance = this;

        // Save Default Configuration
        saveDefaultConfig();

        // Save Default Warps Storage File
        saveDefaultWarpsConfig();

        // Save Help File
        File helpFile = new File(getDataFolder(), "help.txt");
        if (!helpFile.exists()) {
            saveResource("help.txt", false);
        }
        try {
            Files.lines(FileSystems.getDefault().getPath(helpFile.getPath())).forEach(helpLine -> help.add(ChatColor.translateAlternateColorCodes('&', helpLine)));
        } catch (IOException e) {
            getLogger().severe("Could not get help!");
        }

        // Save Rules File
        File rulesFile = new File(getDataFolder(), "rules.txt");
        if (!rulesFile.exists()) {
            saveResource("rules.txt", false);
        }
        try {
            Files.lines(FileSystems.getDefault().getPath(rulesFile.getPath())).forEach(ruleLine -> rules.add(ChatColor.translateAlternateColorCodes('&', ruleLine)));
        } catch (IOException e) {
            getLogger().severe("Could not get rules!");
        }

        // Save Book File
        File bookFile = new File(getDataFolder(), "book.txt");
        if (!bookFile.exists()) {
            saveResource("book.txt", false);
        }
        try {
            Files.lines(FileSystems.getDefault().getPath(bookFile.getPath())).forEach(bookLine -> book += "\n" + ChatColor.translateAlternateColorCodes('&', bookLine));
        } catch (IOException e) {
            getLogger().severe("Could not get book!");
        }

        // Connect to MySQL
        mySQL = new MySQL(
                getConfig().getString("Database.Host"),
                getConfig().getInt("Database.Port"),
                getConfig().getString("Database.Database"),
                getConfig().getString("Database.Username"),
                getConfig().getString("Database.Password")
        );

        // Load Modules
        new KitPvp(this);
        new Donations(this);
        new Administration(this);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateRunnable(this), 200, 200);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> getMySQL().savePlayerData(player.getUniqueId()));

        // Close MySQL Connection
        if (mySQL.getConnection() != null) {
            try {
                mySQL.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get player data from MySQL
     *
     * @param player Player to get data of
     * @return Data of player
     */
    public PlayerData getPlayerData(OfflinePlayer player) {
        return mySQL.getPlayerData(player.getUniqueId());
    }

    /**
     * Respawns a player
     *
     * @param player Player to respawn
     */
    public void respawn(Player player) {
        player.spigot().respawn();
        player.teleport(player.getWorld().getSpawnLocation());
        getServer().getPluginManager().callEvent(new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false));
    }

    /**
     * Saves the default currentWarps configuration file
     */
    private void saveDefaultWarpsConfig() {
        if (warpsConfigFile == null) {
            warpsConfigFile = new File(getDataFolder(), "warps.yml");
        }

        if (!warpsConfigFile.exists()) {
            saveResource("warps.yml", false);
        }

        warpsConfig = YamlConfiguration.loadConfiguration(warpsConfigFile);
    }

    /**
     * Saves the currentWarps configuration file
     */
    public void saveWarpsConfig() {
        try {
            warpsConfig.save(warpsConfigFile);
        } catch (IOException e) {
            getLogger().severe("Could not save warps configuration!");
        }
    }

    /**
     * Gets the location of a warp
     *
     * @param warp Name of warp to get location of
     * @return Location of warp
     */
    public Location getWarpLocation(String warp) {
        ConfigurationSection noSoupSection;

        try {
            noSoupSection = warpsConfig.getConfigurationSection(warpsConfig.getKeys(false).stream().filter(key -> key.equalsIgnoreCase(warp)).findFirst().get());
        } catch (NoSuchElementException ignored) {
            return null;
        }

        return new Location(
                getServer().getWorld(noSoupSection.getString("World")),
                noSoupSection.getDouble("X"),
                noSoupSection.getDouble("Y"),
                noSoupSection.getDouble("Z"),
                (float) noSoupSection.getDouble("Yaw"),
                (float) noSoupSection.getDouble("Pitch")
        );
    }

    /**
     * Warps a player to specified warp
     *
     * @param player Player to warp
     */
    public void warp(String warp, Player player) {
        Location location = getWarpLocation(warp);

        if (location == null) {
            player.sendMessage(ChatColor.RED + "That warp does not exist!");
            return;
        }

        respawn(player);
        player.teleport(location);
        currentWarps.put(player.getUniqueId(), getWarpLocation(warp));

        if (warp.equalsIgnoreCase("Fair")) {
            new PvpKit().wearCheckLevel(player);
        }
    }
}
