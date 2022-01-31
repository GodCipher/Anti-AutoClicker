package de.luzifer.core.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import de.luzifer.core.Core;
import org.bukkit.plugin.Plugin;

public class ArmAnimationListener implements PacketListener {
    
    @Override
    public void onPacketSending(PacketEvent event) {
        
        if (event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION) {
            // click
        }
    }
    
    @Override
    public void onPacketReceiving(PacketEvent event) {
        // unused
    }
    
    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return null;
    }
    
    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }
    
    @Override
    public Plugin getPlugin() {
        return Core.getInstance();
    }
}
