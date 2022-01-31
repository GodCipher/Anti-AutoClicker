package de.luzifer.core.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.luzifer.core.model.repositories.UserRepository;
import de.luzifer.core.model.user.User;
import org.bukkit.plugin.Plugin;

public class ArmAnimationListener extends PacketAdapter {
    
    private final UserRepository userRepository;
    
    public ArmAnimationListener(Plugin plugin, UserRepository userRepository, PacketType... types) {
        super(plugin, types);
        
        this.userRepository = userRepository;
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        
        User user = userRepository.read(event.getPlayer().getUniqueId());
        user.addClicks(1);
    }
}
