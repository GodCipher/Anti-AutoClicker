package de.luzifer.core.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.inject.Inject;
import de.luzifer.core.model.repositories.UserRepository;
import de.luzifer.core.model.user.User;
import org.bukkit.plugin.Plugin;

public class ArmAnimationListener extends PacketAdapter {
    
    @Inject
    private UserRepository userRepository;
    
    public ArmAnimationListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        
        User user = userRepository.read(event.getPlayer().getUniqueId());
        user.addClicks(1);
    }
}
