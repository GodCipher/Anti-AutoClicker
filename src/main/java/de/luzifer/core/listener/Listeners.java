package de.luzifer.core.listener;

import de.luzifer.core.Core;
import de.luzifer.core.api.player.User;
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
import org.bukkit.event.player.*;

import java.util.Objects;

public class Listeners implements Listener {

    private Core core;
    public Listeners(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent e) {
        if(User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if(User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent e) {
        if (User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
        if (Variables.bypass) {
            if ((e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) || e.getPlayer().isOp())
                    || e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) && e.getPlayer().isOp())
                return;
        }

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
        if(e.getDamager() instanceof Player) {
            if(e.getEntity() instanceof LivingEntity) {
                Player player = (Player) e.getDamager();

                if(User.get(player.getUniqueId()).isRestricted()) {
                    e.setCancelled(true);
                }

                if(!Bukkit.getServer().getVersion().contains("1.8")) {

                    if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        return;
                    }

                }

                if(Variables.bypass) {
                    if((player.hasPermission(Objects.requireNonNull(Variables.perms)) || player.isOp())
                            || player.hasPermission(Objects.requireNonNull(Variables.perms)) && player.isOp() ) return;
                }
                if(!Core.lowTPS) {
                    if(Variables.pingChecker) {
                        if(!(User.get(player.getUniqueId()).getPing() >= Variables.highestAllowedPing)) {
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

        if(Variables.autoNotify) {
            Bukkit.getScheduler().runTaskLater(core, () -> {
                if((p.hasPermission(Objects.requireNonNull(Variables.perms)) || p.isOp())
                        || p.hasPermission(Objects.requireNonNull(Variables.perms)) && p.isOp() ) {

                    user.setNotified(true);

                    p.sendMessage(" ");
                    Variables.NOTIFY_ACTIVATED.forEach(var -> p.sendMessage(Core.prefix + var.replace("&", "§")));
                    p.sendMessage(" ");
                }
            }, 15);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(User.get(all.getUniqueId()).getChecked() == User.get(p.getUniqueId())) {

                all.sendMessage(" ");
                Variables.PLAYER_NOW_OFFLINE.forEach(var -> all.sendMessage(Core.prefix + var.replace("&", "§").replaceAll("%player%", p.getName())));
                all.sendMessage(" ");

                User.get(all.getUniqueId()).setChecked(null);

            }
        }
        User.getAllUser().remove(User.get(p.getUniqueId()));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(User.get(e.getPlayer().getUniqueId()).isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onNormalClick(PlayerInteractEvent e) {
        if(User.get(e.getPlayer().getUniqueId()).isRestricted()) {
            e.setCancelled(true);
        }
        if(Variables.bypass) {
            if((e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) || e.getPlayer().isOp())
                    || e.getPlayer().hasPermission(Objects.requireNonNull(Variables.perms)) && e.getPlayer().isOp() )  return;
        }
        if(e.getAction() == Action.LEFT_CLICK_AIR ||
                e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if(!Core.lowTPS) {
                if(Variables.pingChecker) {
                    if(!(User.get(e.getPlayer().getUniqueId()).getPing() >= Variables.highestAllowedPing)) {
                        User.get(e.getPlayer().getUniqueId()).addClicks(1);
                    }
                } else {
                    User.get(e.getPlayer().getUniqueId()).addClicks(1);
                }
            }
        }
    }
}
