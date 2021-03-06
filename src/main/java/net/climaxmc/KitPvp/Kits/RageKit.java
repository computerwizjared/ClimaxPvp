package net.climaxmc.KitPvp.Kits;

import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.Kit;
import net.climaxmc.KitPvp.KitManager;
import net.climaxmc.KitPvp.Utils.Ability;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class RageKit extends Kit {

    private final int cooldown = 12;
    private ItemStack ability = new ItemStack(Material.MAGMA_CREAM);

    private Ability rage = new Ability("Rage", 1, cooldown, TimeUnit.SECONDS);

    public RageKit() {
        super("Rage", new ItemStack(Material.MAGMA_CREAM), "Use your Rage Ability to take down Enemies!", ChatColor.RED);
    }

    protected void wear(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        player.getInventory().addItem(sword);
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        helm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        helm.addEnchantment(Enchantment.DURABILITY, 2);
        LeatherArmorMeta helmmeta = (LeatherArmorMeta) helm.getItemMeta();
        helmmeta.setColor(Color.BLUE);
        helm.setItemMeta(helmmeta);
        player.getInventory().setHelmet(helm);
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        boots.addEnchantment(Enchantment.DURABILITY, 2);
        LeatherArmorMeta bootsmeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsmeta.setColor(Color.BLUE);
        boots.setItemMeta(bootsmeta);
        player.getInventory().setBoots(boots);

        ItemMeta magmameta = ability.getItemMeta();
        magmameta.setDisplayName(ChatColor.AQUA + "Rage \u00A7f» \u00A78[\u00A76" + cooldown + "\u00A78]");
        ability.setItemMeta(magmameta);
        player.getInventory().addItem(ability);

        addSoup(player.getInventory(), 2, 35);
    }

    protected void wearNoSoup(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        ItemStack sword = new ItemStack(Material.WOOD_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        player.getInventory().addItem(sword);
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        helm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        LeatherArmorMeta helmmeta = (LeatherArmorMeta) helm.getItemMeta();
        helmmeta.setColor(Color.BLUE);
        helm.setItemMeta(helmmeta);
        player.getInventory().setHelmet(helm);
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        LeatherArmorMeta bootsmeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsmeta.setColor(Color.BLUE);
        boots.setItemMeta(bootsmeta);
        player.getInventory().setBoots(boots);

        ItemMeta magmameta = ability.getItemMeta();
        magmameta.setDisplayName(ChatColor.AQUA + "Rage \u00A7f» \u00A78[\u00A76" + cooldown + "\u00A78]");
        ability.setItemMeta(magmameta);
        player.getInventory().addItem(ability);

        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        player.getInventory().addItem(rod);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (KitManager.isPlayerInKit(player, this)) {
            if (player.getInventory().getItemInHand().getType() == Material.MAGMA_CREAM) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (!rage.tryUse(player)) {
                        return;
                    }
                    player.sendMessage(ChatColor.GOLD + "You used the " + ChatColor.AQUA + "Rage" + ChatColor.GOLD + " Ability!");
                    player.getWorld().playSound(player.getLocation(), Sound.GHAST_SCREAM, 3, 1);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 80, 1));
                    ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta helmmeta = (LeatherArmorMeta) helm.getItemMeta();
                    helmmeta.setColor(Color.RED);
                    helm.setItemMeta(helmmeta);
                    player.getInventory().setHelmet(helm);
                    player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta bootsmeta = (LeatherArmorMeta) boots.getItemMeta();
                    bootsmeta.setColor(Color.RED);
                    boots.setItemMeta(bootsmeta);
                    player.getInventory().setBoots(boots);
                    Bukkit.getServer().getScheduler().runTaskLater(ClimaxPvp.getInstance(), () -> {
                        if (KitManager.isPlayerInKit(player, this)) {
                            player.removePotionEffect(PotionEffectType.SPEED);
                            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                            player.removePotionEffect(PotionEffectType.JUMP);
                            ItemStack helm2 = new ItemStack(Material.LEATHER_HELMET);
                            helm2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                            helm2.addEnchantment(Enchantment.DURABILITY, 2);
                            LeatherArmorMeta helmmeta2 = (LeatherArmorMeta) helm2.getItemMeta();
                            helmmeta2.setColor(Color.BLUE);
                            helm2.setItemMeta(helmmeta2);
                            player.getInventory().setHelmet(helm2);
                            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                            ItemStack boots2 = new ItemStack(Material.LEATHER_BOOTS);
                            boots2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                            boots2.addEnchantment(Enchantment.DURABILITY, 2);
                            LeatherArmorMeta bootsmeta2 = (LeatherArmorMeta) boots2.getItemMeta();
                            bootsmeta2.setColor(Color.BLUE);
                            boots2.setItemMeta(bootsmeta2);
                            player.getInventory().setBoots(boots2);
                        }
                    }, 100);

                    rage.startCooldown(player, this, cooldown, ability);
                }
            }
        }
    }
}
