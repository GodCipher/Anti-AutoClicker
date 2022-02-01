package de.luzifer.core.model.punishment.implementations;

import de.luzifer.core.model.punishment.Punishment;
import de.luzifer.core.model.user.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KickPunishment implements Punishment {

    @Override
    public void punish(User user) {

        Player player = Bukkit.getPlayer(user.getUuid());

        if (Variables.executeKickCommand == null || Variables.executeKickCommand.isEmpty()) {

            List<String> reasons = new ArrayList<>(Variables.KICK_REASON);
            player.kickPlayer("§cAnti§4AC \n " + String.join("\n ", reasons).replace("&", "§"));

        } else {

            String command = Variables.executeKickCommand;
            command = command.replace("%player%", player.getName()).replace("&", "§");

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
