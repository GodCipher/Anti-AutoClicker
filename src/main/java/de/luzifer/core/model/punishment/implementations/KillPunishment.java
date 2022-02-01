package de.luzifer.core.model.punishment.implementations;

import de.luzifer.core.model.punishment.Punishment;
import de.luzifer.core.model.user.User;
import org.bukkit.Bukkit;

public class KillPunishment implements Punishment {

    @Override
    public void punish(User user) {
        //noinspection ConstantConditions
        Bukkit.getPlayer(user.getUuid()).setHealth(0);
    }
}
