package de.luzifer.core.listener.bukkit;

import de.luzifer.core.Core;
import de.luzifer.core.model.repositories.UserRepository;
import de.luzifer.core.model.user.User;
import de.luzifer.core.utils.Variables;
import lombok.AllArgsConstructor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

@AllArgsConstructor
public class ConnectionListener implements Listener {
    
    private final UserRepository userRepository;
    
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
    public void onLogin(PlayerLoginEvent e) {
        if (e.getPlayer().isBanned()) {
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, Bukkit.getBanList(BanList.Type.NAME).getBanEntry(e.getPlayer().getName()).getReason());
        }
    }
}
