package de.luzifer.core.listener.bukkit;

import de.luzifer.core.model.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RestrictionListener implements Listener {
    
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
    public void onMove(PlayerMoveEvent e) {
        if (User.get(e.getPlayer().getUniqueId()).isFrozen()) {
            e.setCancelled(true);
        }
    }
}
