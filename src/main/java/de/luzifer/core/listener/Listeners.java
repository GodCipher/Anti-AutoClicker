package de.luzifer.core.listener;

import com.cryptomorin.xseries.XMaterial;
import de.luzifer.core.Core;
import de.luzifer.core.api.player.User;
import de.luzifer.core.api.profile.inventory.pagesystem.Menu;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Listeners implements Listener {
    
    private static final XMaterial[] INTERACTIVE_MATERIALS = {
            XMaterial.FISHING_ROD, XMaterial.EGG, XMaterial.SNOWBALL, XMaterial.ENDER_PEARL, XMaterial.ENDER_EYE,
            XMaterial.SPLASH_POTION, XMaterial.LINGERING_POTION, XMaterial.EXPERIENCE_BOTTLE, XMaterial.FIREWORK_ROCKET
    };
    
    private final Core core;
    
    // not really a good name
    private final List<UUID> recently_clicked = new ArrayList<>();
    
    public Listeners(Core core) {
        this.core = core;
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent e) {
        
        Player p = e.getPlayer();
        
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (User.get(all.getUniqueId()).getChecked() == User.get(p.getUniqueId())) {
                
                Variables.PLAYER_NOW_OFFLINE.forEach(var -> all.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", p.getName())));
                User.get(all.getUniqueId()).setChecked(null);
                
            }
        }
        User.getAllUser().remove(User.get(p.getUniqueId()));
    }
    
    @EventHandler
    public void onBuild(BlockPlaceEvent e) {
        if (User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        
        if (e.getView().getTopInventory().getHolder() instanceof Menu) {
            
            Menu menu = (Menu) e.getView().getTopInventory().getHolder();
            menu.handleEvent(e);
        }
    }
    
    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent e) {
    
        if (Variables.bypass)
            if ((e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) || e.getPlayer().isOp()) || e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) && e.getPlayer().isOp())
                return;
            
        if (getBukkitVersion() > 8)
            if (e.getHand() == EquipmentSlot.OFF_HAND) return;
    
        if (User.get(e.getPlayer().getUniqueId()).isRestricted())
            e.setCancelled(true);
        
        if (!Core.lowTps) {
            if (Variables.pingChecker) {
                if (!(User.get(e.getPlayer().getUniqueId()).getPing() >= Variables.highestAllowedPing)) {
                    if (User.get(e.getPlayer().getUniqueId()).getLastRightClick() == null) {
                        User.get(e.getPlayer().getUniqueId()).setLastRightClick(System.currentTimeMillis());
                    }
                    if (User.get(e.getPlayer().getUniqueId()).getClicks() <= 15) {
                        if (!(System.currentTimeMillis() - User.get(e.getPlayer().getUniqueId()).getLastRightClick() <= 1)) {
                            if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
                                User.get(e.getPlayer().getUniqueId()).addClicks(1);
                            }
                        }
                    } else {
                        User.get(e.getPlayer().getUniqueId()).addClicks(1);
                    }
                    User.get(e.getPlayer().getUniqueId()).setLastRightClick(System.currentTimeMillis());
                }
            } else {
                if (User.get(e.getPlayer().getUniqueId()).getLastRightClick() == null) {
                    User.get(e.getPlayer().getUniqueId()).setLastRightClick(System.currentTimeMillis());
                }
                if (User.get(e.getPlayer().getUniqueId()).getClicks() <= 15) {
                    if (!(System.currentTimeMillis() - User.get(e.getPlayer().getUniqueId()).getLastRightClick() <= 1)) {
                        if (e.getPlayer().getItemInHand().getType() == Material.AIR) {
                            User.get(e.getPlayer().getUniqueId()).addClicks(1);
                        }
                    }
                } else {
                    User.get(e.getPlayer().getUniqueId()).addClicks(1);
                }
                User.get(e.getPlayer().getUniqueId()).setLastRightClick(System.currentTimeMillis());
            }
        }
    }
    
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && !e.getDamager().hasMetadata("NPC")) {
            if (e.getEntity() instanceof LivingEntity) {
                Player player = (Player) e.getDamager();
  
                if (Variables.bypass)
                    if ((player.hasPermission(Objects.requireNonNull(Variables.perms)) || player.isOp()) || player.hasPermission(Objects.requireNonNull(Variables.perms)) && player.isOp())
                        return;
    
                if (User.get(player.getUniqueId()).isRestricted())
                    e.setCancelled(true);
    
                if (getBukkitVersion() > 8) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
                }
                
                if (!Core.lowTps) {
                    if (Variables.pingChecker) {
                        if (!(User.get(player.getUniqueId()).getPing() >= Variables.highestAllowedPing)) {
                            User.get(player.getUniqueId()).addClicks(1);
                        }
                    } else {
                        User.get(player.getUniqueId()).addClicks(1);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        User user = User.get(p.getUniqueId());
        
        if (Variables.autoNotify) {
            Bukkit.getScheduler().runTaskLater(core, () -> {
                if (hasSubPermission(p, "notify")) {
                    
                    user.setNotified(true);
                    Variables.NOTIFY_ACTIVATED.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                }
            }, 15);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (User.get(all.getUniqueId()).getChecked() == User.get(p.getUniqueId())) {
                
                Variables.PLAYER_NOW_OFFLINE.forEach(var -> all.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", p.getName())));
                User.get(all.getUniqueId()).setChecked(null);
            }
        }
        User.getAllUser().remove(User.get(p.getUniqueId()));
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (User.get(e.getPlayer().getUniqueId()).isFrozen()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onNormalClick(PlayerInteractEvent e) {
    
        if (Variables.bypass)
            if ((e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) || e.getPlayer().isOp()) || e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) && e.getPlayer().isOp())
                return;
            
        if (User.get(e.getPlayer().getUniqueId()).isRestricted())
            e.setCancelled(true);
    
        if (getBukkitVersion() > 8 && e.getHand() == EquipmentSlot.OFF_HAND)
            return;
            
        if(e.getItem() != null) {
            
            boolean isInteractiveItem = false;
            for(XMaterial material : INTERACTIVE_MATERIALS) {
                if(e.getItem().getType() == material.parseMaterial()) {
                    isInteractiveItem = true;
                    break;
                }
            }
            
            if(isInteractiveItem && cancelDuplicateClick(e.getPlayer().getUniqueId(), e.getAction()))
                return;
        }
        
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            
            if (!Core.lowTps) {
                if (Variables.pingChecker) {
                    if (!(User.get(e.getPlayer().getUniqueId()).getPing() >= Variables.highestAllowedPing)) {
                        User.get(e.getPlayer().getUniqueId()).addClicks(1);
                    }
                } else {
                    User.get(e.getPlayer().getUniqueId()).addClicks(1);
                }
            }
        }
    }
    
    private boolean cancelDuplicateClick(UUID uuid, Action action) {
    
        if(recently_clicked.contains(uuid)) {
            recently_clicked.remove(uuid);
            return true;
        }
        
        if(action == Action.RIGHT_CLICK_AIR)
            recently_clicked.add(uuid);
        
        return false;
    }
    
    private double getBukkitVersion() {
        
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }
    
    private boolean hasSubPermission(Player player, String perms) {
        return player.hasPermission(Variables.perms + "." + perms) || hasPermission(player);
    }
    
    private boolean hasPermission(Player player) {
        return player.hasPermission(Variables.perms + ".*") || player.isOp();
    }
}
