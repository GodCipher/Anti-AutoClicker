package dev.luzifer.antiac.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import dev.luzifer.antiac.Core;
import dev.luzifer.antiac.api.player.User;
import dev.luzifer.antiac.utils.Variables;
import org.bukkit.entity.Player;

import java.util.Objects;

public class InteractEntityPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interactEntityPacked = new WrapperPlayClientInteractEntity(event);
            if(interactEntityPacked.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

            Player player = (Player) event.getPlayer();

            if (Variables.bypass)
                if ((player.hasPermission(Objects.requireNonNull(Variables.perms))
                        || player.isOp()) || player.hasPermission(Objects.requireNonNull(Variables.perms)) && player.isOp())
                    return;

            if (!Core.lowTps) {
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
