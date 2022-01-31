package de.luzifer.core.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

public class ArmAnimationListener extends PacketAdapter {
    
    public ArmAnimationListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        // click
    }
}
