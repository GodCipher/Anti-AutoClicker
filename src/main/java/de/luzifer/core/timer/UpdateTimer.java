package de.luzifer.core.timer;

import de.luzifer.core.Core;
import de.luzifer.core.utils.UpdateChecker;
import de.luzifer.core.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UpdateTimer implements Runnable{

    private Core core;
    public UpdateTimer(Core core) {
        this.core = core;
    }

    @Override
    public void run() {
        new UpdateChecker(core, 74933).getVersion(version -> {
            if (!core.getDescription().getVersion().equals(version)) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!player.hasPermission(Variables.perms) && !player.isOp()) {

                    } else {
                        player.sendMessage(" ");
                        player.sendMessage(Core.prefix + "§aAn update is available!");
                        player.sendMessage(Core.prefix + "§c" + Core.getInstance().getDescription().getVersion() + " §e-> §a" + version);
                        player.sendMessage(" ");
                    }
                }
            }
        });
    }
}
