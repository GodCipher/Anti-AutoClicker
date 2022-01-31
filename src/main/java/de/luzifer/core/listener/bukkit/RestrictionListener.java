package de.luzifer.core.listener.bukkit;

import de.luzifer.core.model.repositories.UserRepository;
import de.luzifer.core.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RestrictionListener implements Listener {
    
    private UserRepository userRepository;
    
    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        
        User user = userRepository.read(event.getPlayer().getUniqueId());
        
        if (user.isRestricted())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
    
        User user = userRepository.read(event.getPlayer().getUniqueId());
        
        if (user.isRestricted())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
    
        User user = userRepository.read(event.getPlayer().getUniqueId());
        
        if (user.isRestricted())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    
        User user = userRepository.read(event.getPlayer().getUniqueId());
        
        if (user.isRestricted())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onInteractWithEntity(PlayerInteractAtEntityEvent event) {
    
        User user = userRepository.read(event.getPlayer().getUniqueId());
        
        if (user.isRestricted())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        
        if(!(event.getDamager() instanceof Player))
            return;
        
        Player player = (Player) event.getEntity();
        User user = userRepository.read(player.getUniqueId());
        
        if (user.isRestricted())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
    
        User user = userRepository.read(event.getPlayer().getUniqueId());
        
        if (user.isFrozen())
            event.setCancelled(true);
    }
}
