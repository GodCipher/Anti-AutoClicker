package de.luzifer.core.api.check;

import de.luzifer.core.api.player.User;
import org.bukkit.plugin.Plugin;

public abstract class Check {

    public abstract void onEnable(Plugin plugin);

    public abstract void execute(User user);

}
