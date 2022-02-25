package de.luzifer.core.listener.bukkit;

import com.cryptomorin.xseries.XMaterial;
import de.luzifer.core.Core;
import de.luzifer.core.model.user.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Deprecated
public class Listeners implements Listener {
    
    private final Core core;
    
    private final List<UUID> rod_click = new ArrayList<>();
    
    public Listeners(Core core) {
        this.core = core;
    }
    
    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent e) {
    
        if (Variables.bypass)
            if ((e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) || e.getPlayer().isOp()) || e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) && e.getPlayer().isOp())
                return;
            
        if (getBukkitVersion() > 8)
            if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        
        if (!Core.lowTPS) {
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
    
                if (getBukkitVersion() > 8) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
                }
                
                if (!Core.lowTPS) {
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
    public void onNormalClick(PlayerInteractEvent e) {
    
        if (Variables.bypass)
            if ((e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) || e.getPlayer().isOp()) || e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) && e.getPlayer().isOp())
                return;
    
        if (getBukkitVersion() > 8)
            if (e.getHand() == EquipmentSlot.OFF_HAND) return;
            
        if(e.getItem() != null && e.getItem().getType() == XMaterial.FISHING_ROD.parseMaterial())
            if(cancelledDuplicateRodClick(e.getPlayer().getUniqueId(), e.getAction()))
                return;
        
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            
            if (!Core.lowTPS) {
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
    
    private boolean cancelledDuplicateRodClick(UUID uuid, Action action) {
        
        if(action == Action.RIGHT_CLICK_AIR) {
            rod_click.add(uuid);
        } else if(action == Action.LEFT_CLICK_AIR) {
            
            if(rod_click.contains(uuid)) {
                rod_click.remove(uuid);
                return true;
            }
        }
        
        return false;
    }
    
    private double getBukkitVersion() {
        
        String version = Bukkit.getBukkitVersion().split("-")[0];
        return Double.parseDouble(version.split("\\.")[1]);
    }

}
