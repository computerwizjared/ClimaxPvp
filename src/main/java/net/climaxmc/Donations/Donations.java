package net.climaxmc.Donations;

import lombok.Getter;
import net.climaxmc.ClimaxPvp;
import net.climaxmc.Donations.Commands.ParticlesCommand;
import net.climaxmc.Donations.Listeners.InventoryClickListener;
import net.climaxmc.Donations.Listeners.PlayerMoveListener;
import net.climaxmc.Utils.ParticleEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Donations {
    @Getter private HashMap<UUID, ParticleEffect.ParticleType> particlesEnabled = new HashMap<UUID, ParticleEffect.ParticleType>();

    public Donations(ClimaxPvp plugin) {
        plugin.getCommand("particles").setExecutor(new ParticlesCommand(plugin));
        plugin.getServer().getPluginManager().registerEvents(new PlayerMoveListener(plugin, this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InventoryClickListener(plugin, this), plugin);
    }
}
