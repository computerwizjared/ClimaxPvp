package net.climaxmc.KitPvp.Kits;

import net.climaxmc.KitPvp.Kit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class GhastKit extends Kit {
    ArrayList<UUID> ghast = new ArrayList<UUID>();

    public GhastKit() {
        super("Ghast", new ItemStack(Material.FIREBALL), "Set the world on Fire with the Ghast Kit!", ChatColor.BLUE);
    }

    public void wear(Player player) {
    	ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    	sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
        player.getInventory().addItem(sword);
        ItemStack hoe = new ItemStack(Material.GOLD_HOE);
        ItemMeta hoemeta = hoe.getItemMeta();
        hoemeta.setDisplayName(ChatColor.RED + "Fireball Launcher");
        hoe.setItemMeta(hoemeta);
        player.getInventory().addItem(hoe);
        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 3);
        player.getInventory().setBoots(boots);
        addSoup(player.getInventory(), 2, 35);
        ghast.add(player.getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (ghast.contains(player.getUniqueId())) {
            if (player.getInventory().getItemInHand().getType() == Material.GOLD_HOE) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                	Fireball f = event.getPlayer().launchProjectile(Fireball.class);
                	f.setIsIncendiary(false);
                	double vel = f.getVelocity().length() * (0.1D + 0.1D * 5);
                    // Knock player back
                    velocity(player, player.getLocation().getDirection().multiply(-1), vel,
                            false, 0.0D, 0.2D, 0.8D, true);
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamge(EntityDamageByEntityEvent event){
    	if(event.getDamager() instanceof Fireball){
    		Fireball f = (Fireball) event.getDamager();
    		if(f.getShooter() instanceof Player){
    			Player shooter = (Player) f.getShooter();
    			if(ghast.contains(shooter.getUniqueId())){
    				if(shooter.getItemInHand().getType() == Material.GOLD_HOE){
    					event.setDamage(20.0);
    				}
    			}
    		}
    	}
    }
    
    private void velocity(Entity ent, Vector vec, double str, boolean ySet, double yBase, double yAdd, double yMax, boolean groundBoost)
    {
        if ((Double.isNaN(vec.getX())) || (Double.isNaN(vec.getY())) || (Double.isNaN(vec.getZ())) || (vec.length() == 0.0D)) {
            return;
        }

        if (ySet) {
            vec.setY(yBase);
        }

        vec.normalize();
        vec.multiply(str);


        vec.setY(vec.getY() + yAdd);


        if (vec.getY() > yMax) {
            vec.setY(yMax);
        }
        if (groundBoost) {
            vec.setY(vec.getY() + 0.2D);
        }

        ent.setFallDistance(0.0F);
        ent.setVelocity(vec);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (ghast.contains(player.getUniqueId())) {
        	ghast.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ghast.contains(player.getUniqueId())) {
        	ghast.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ghast.contains(player.getUniqueId())) {
        	ghast.remove(player.getUniqueId());
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
    	Player player = event.getPlayer();
    	if(ghast.contains(player.getUniqueId())){
    		ghast.remove(player.getUniqueId());
    	}
    }
}
