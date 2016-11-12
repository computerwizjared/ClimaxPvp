package net.climaxmc.KitPvp.Listeners;

import net.climaxmc.ClimaxPvp;
import net.climaxmc.KitPvp.Commands.ReportCommand;
import net.climaxmc.KitPvp.Commands.TeamCommand;
import net.climaxmc.KitPvp.KitManager;
import net.climaxmc.KitPvp.Kits.BomberKit;
import net.climaxmc.KitPvp.Kits.FighterKit;
import net.climaxmc.KitPvp.Menus.ReportGUI;
import net.climaxmc.KitPvp.Utils.Challenges.Challenge;
import net.climaxmc.KitPvp.Utils.Teams.TeamMessages;
import net.climaxmc.KitPvp.Utils.Teams.TeamUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class InventoryClickListener implements Listener {
    private ClimaxPvp plugin;

    public InventoryClickListener(ClimaxPvp plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        final Player player = (Player) event.getWhoClicked();

        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            if (event.getSlotType() == null || event.getCurrentItem() == null
                    || event.getCurrentItem().getType() == null) {
                return;
            }
            if (inventory == player.getInventory()) {
                event.setCancelled(true);
            }

            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null) {
                    event.setCancelled(true);
                }
                if (event.getCurrentItem().equals(itemStack)) {
                    event.setCancelled(true);
                }
            }

            /*if (event.getClickedInventory().equals(player.getInventory()) && !event.getCurrentItem().getType().equals(Material.MUSHROOM_SOUP)) {
                event.setCancelled(true);
            }*/

            if ((KitManager.isPlayerInKit(player, BomberKit.class)
                    && event.getAction().equals(InventoryAction.HOTBAR_SWAP))
                    || event.getCursor().getType().equals(Material.TNT)
                    || event.getCurrentItem().getType().equals(Material.TNT)) {
                event.setCancelled(true);
            }

            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null
                    && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
                if (inventory.getName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Kit Selector")) {
                    KitManager.getKits().stream().filter(kit -> event.getCurrentItem().getItemMeta().getDisplayName()
                            .equals(kit.getItem().getItemMeta().getDisplayName())).forEach(kit -> {
                        kit.wearCheckLevel(player);
                        plugin.getServer().getScheduler().runTask(plugin, player::closeInventory);
                    });
                    event.setCancelled(true);
                }
            }

            if (inventory.getName().equals("Challenges")) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem.getType().equals(Material.MAP)) {
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Daily Challenge #1")) {
                        plugin.getChallengesFiles().setStarted(player, Challenge.Daily1);
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 2);
                        player.closeInventory();
                        player.sendMessage(ChatColor.YELLOW + " You've started " + ChatColor.AQUA + "Daily Challenge #1");
                    }
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Daily Challenge #2")) {
                        plugin.getChallengesFiles().setStarted(player, Challenge.Daily2);
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 2);
                        player.closeInventory();
                        player.sendMessage(ChatColor.YELLOW + " You've started " + ChatColor.AQUA + "Daily Challenge #2");
                    }
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Daily Challenge #3")) {
                        plugin.getChallengesFiles().setStarted(player, Challenge.Daily3);
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 2);
                        player.closeInventory();
                        player.sendMessage(ChatColor.YELLOW + " You've started " + ChatColor.AQUA + "Daily Challenge #3");
                    }
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Daily Challenge #4")) {
                        plugin.getChallengesFiles().setStarted(player, Challenge.Daily4);
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 2);
                        player.closeInventory();
                        player.sendMessage(ChatColor.YELLOW + " You've started " + ChatColor.AQUA + "Daily Challenge #4");
                    }
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Weekly Challenge #1")) {
                        plugin.getChallengesFiles().setStarted(player, Challenge.Weekly1);
                        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 2);
                        player.closeInventory();
                        player.sendMessage(ChatColor.YELLOW + " You've started " + ChatColor.AQUA + "Weekly Challenge #1");
                    }
                    if (clickedItem.getEnchantments() != null) {
                       player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                    }
                }

                if (clickedItem.getType().equals(Material.PAPER)) {
                    player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                    long cooldown = plugin.getChallengesFiles().getChallenge(clickedItem.getItemMeta().getDisplayName()).getCooldownTime()
                            - ((System.currentTimeMillis() / 1000)
                            - (plugin.getChallengesFiles().getCompletedTime(player,
                            plugin.getChallengesFiles().getChallenge(clickedItem.getItemMeta().getDisplayName()))));
                    if (cooldown >= 86400) {
                        cooldown = ((cooldown / 60) / 60) / 24;
                        if (cooldown == 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " day to start this challenge again!");
                        if (cooldown > 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " days to start this challenge again!");
                    } else if (cooldown >= 3600) {
                        cooldown = (cooldown / 60) / 60;
                        if (cooldown == 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " hour to start this challenge again!");
                        if (cooldown > 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " hours to start this challenge again!");
                    } else if (cooldown >= 60) {
                        cooldown = cooldown / 60;
                        if (cooldown == 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " minute to start this challenge again!");
                        if (cooldown > 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " minutes to start this challenge again!");
                    } else {
                        if (cooldown == 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " second to start this challenge again!");
                        if (cooldown > 1)
                            player.sendMessage(ChatColor.RED + " Come back in " + cooldown + " seconds to start this challenge again!");
                    }
                }
                event.setCancelled(true);
            }
            if (event.getClickedInventory().getName().contains("Climax Reports")) {
                ItemStack item = event.getCurrentItem();
                ReportGUI reportGUI = new ReportGUI(plugin);
                String targetName = inventory.getName().replace("Climax Reports: ", "");
                Player target = plugin.getServer().getPlayerExact(targetName);
                int slot = event.getSlot();
                String message = ReportCommand.getReportBuilders().get(player.getUniqueId());
                ArrayList<String> rm = ReportCommand.getReportArray().get(player.getUniqueId());

                if (item.getType().equals(Material.BOOK)) {
                    rm.add(item.getItemMeta().getDisplayName().replace(ChatColor.RED + "", ""));
                    int size = ReportCommand.getReportArray().get(player.getUniqueId()).size();
                    for (int i = 0; i <= size - 1; i++) {
                        if (message != null) {
                            message = message + ", " + rm.get(i);
                        } else {
                            message = rm.get(i);
                        }
                    }
                    reportGUI.setWool(inventory, target, message);
                    inventory.setItem(slot, reportGUI.setSelected(item));
                }
                if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                    rm.remove(item.getItemMeta().getDisplayName().replace(ChatColor.GREEN + "", ""));
                    message = null;
                    int size = ReportCommand.getReportArray().get(player.getUniqueId()).size();
                    for (int i = 0; i <= size - 1; i++) {
                        if (message != null) {
                            message = message + ", " + rm.get(i);
                        } else {
                            message = rm.get(i);
                        }
                    }
                    reportGUI.setWool(inventory, target, message);
                    inventory.setItem(slot, reportGUI.setUnSelected(item));
                }
                if (item.getType().equals(Material.WOOL)) {
                    if (item.getDurability() == 5) {
                        message = null;
                        int size = ReportCommand.getReportArray().get(player.getUniqueId()).size();
                        for (int i = 0; i <= size - 1; i++) {
                            if (message != null) {
                                message = message + ", " + rm.get(i);
                            } else {
                                message = rm.get(i);
                            }
                        }
                        if (message == null) {
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                            player.sendMessage(ChatColor.RED + "You tried to report a player without selecting any report options!" +
                                    " Try selecting report options next time!");
                        } else {
                            reportGUI.report(player, target, message);
                            player.closeInventory();
                        }
                    }
                    if (item.getDurability() == 14) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "Report cancelled.");
                    }
                }
                event.setCancelled(true);
            }

            if (event.getClickedInventory().getName().contains("Options")) {
                Player target = plugin.getServer().getPlayerExact(event.getClickedInventory().getName().substring(12));
                if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
                    ReportGUI reportGUI = new ReportGUI(plugin);
                    if (target == player) {
                        player.sendMessage(ChatColor.RED + "You can't report yourself! Unless you have something to tell us.... *gives suspicious look*");
                        player.closeInventory();
                        return;
                    }
                    ReportCommand.getReportBuilders().put(player.getUniqueId(), null);
                    ReportCommand.getReportArray().put(player.getUniqueId(), new ArrayList<>());
                    reportGUI.openInventory(player, target);
                }

                if (event.getCurrentItem().getType().equals(Material.FEATHER)) {
                    player.closeInventory();
                    TextComponent component = new TextComponent("Click here to message " + target.getName() + "!");
                    BaseComponent baseComponent = component;
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + target.getName() + " "));
                    component.setColor(ChatColor.AQUA);
                    component.setBold(true);
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "/msg " + target.getName() + " <message>").create()));
                    player.spigot().sendMessage(baseComponent);
                }

                if (event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
                    player.closeInventory();
                    if (target == player) {
                        player.sendMessage(ChatColor.RED + "You can't team with yourself!");
                        return;
                    }
                    TeamMessages teamMessages = new TeamMessages(plugin);
                    teamMessages.sendRequestMessage(player, target);
                }

                event.setCancelled(true);
            }

        } else {
            event.setCancelled(false);
        }
        if (event.getClickedInventory().getName().contains("Class Selector")) {
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
                FighterKit kit = new FighterKit();
                kit.wear(player);
            }
            event.setCancelled(true);
        }
    }
}