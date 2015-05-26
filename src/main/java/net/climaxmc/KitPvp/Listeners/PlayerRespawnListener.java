package net.climaxmc.KitPvp.Listeners;

import net.climaxmc.ClimaxPvp;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PlayerRespawnListener implements Listener {
    ClimaxPvp plugin;

    public PlayerRespawnListener(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.teleport(player.getWorld().getSpawnLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20F);
        player.setMaxHealth(20F);
        player.setFlySpeed(0.1F);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        ItemStack kitSelector = new ItemStack(Material.NETHER_STAR);
        ItemMeta kitSelectorMeta = kitSelector.getItemMeta();
        kitSelectorMeta.setDisplayName("§a§lKit Selector");
        List<String> kitSelectorLores = new ArrayList<String>();
        kitSelectorLores.add("§5§o(Right Click) to select a kit!");
        kitSelectorMeta.setLore(kitSelectorLores);
        kitSelector.setItemMeta(kitSelectorMeta);

        ItemStack particles = new ItemStack(Material.SEEDS);
        ItemMeta particlesMeta = particles.getItemMeta();
        particlesMeta.setDisplayName("§a§lTrail Selector");
        List<String> particlesLores = new ArrayList<String>();
        particlesLores.add("§5§o(Right Click) to select a trail!");
        particlesMeta.setLore(particlesLores);
        particles.setItemMeta(particlesMeta);

        player.getInventory().setItem(0, kitSelector);
        player.getInventory().setItem(8, particles);
    }
}
