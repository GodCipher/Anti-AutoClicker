package de.luzifer.core.listener.bukkit;

import de.luzifer.core.Core;
import de.luzifer.core.model.repositories.UserRepository;
import de.luzifer.core.model.user.User;
import de.luzifer.core.utils.Variables;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class ConnectionListener implements Listener {
    
    private final UserRepository userRepository;
    
    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        
        Player p = e.getPlayer();
        User user = User.get(p.getUniqueId());
        
        if (Variables.autoNotify) {
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
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
    
    private boolean hasSubPermission(Player player, String perms) {
        return player.hasPermission(Variables.perms + "." + perms) || hasPermission(player);
    }
    
    private boolean hasPermission(Player player) {
        return player.hasPermission(Variables.perms + ".*") || player.isOp();
    }
}
