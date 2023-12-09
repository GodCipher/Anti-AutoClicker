package dev.luzifer.antiac.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import dev.luzifer.antiac.Core;
import dev.luzifer.antiac.api.player.User;
import dev.luzifer.antiac.utils.Variables;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BlockInteractionPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = (Player) event.getPlayer();
        if(event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            if (Variables.bypass)
                if ((player.hasPermission(Objects.requireNonNull(Variables.perms))
                        || player.isOp()) || player.hasPermission(Objects.requireNonNull(Variables.perms)) && player.isOp())
                    return;

            if(isDuplicateClick(player)) return;

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

    private boolean isDuplicateClick(Player player) {
        User user = User.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        if (user.getLastBlockClick() != null) {
            long lastClickTime = user.getLastBlockClick();

            if (lastClickTime == currentTime)
                user.setBlockClicksInARow(user.getBlockClicksInARow() + 1);
            else
                user.setBlockClicksInARow(1);

            user.setLastBlockClick(currentTime);
            return user.getBlockClicksInARow() > 2;
        }

        user.setLastBlockClick(currentTime);

        return false;
    }
}
