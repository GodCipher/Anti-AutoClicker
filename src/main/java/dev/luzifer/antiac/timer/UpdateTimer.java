package dev.luzifer.antiac.timer;

import dev.luzifer.antiac.Core;
import dev.luzifer.antiac.version.UpdateChecker;
import dev.luzifer.antiac.utils.Variables;
import dev.luzifer.antiac.version.UpdateCheckerResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UpdateTimer implements Runnable {
    
    private final Core core;
    private final UpdateChecker updateChecker;
    
    public UpdateTimer(Core core) {
        
        this.core = core;
        this.updateChecker = new UpdateChecker(core);
    }
    
    @Override
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            
            UpdateCheckerResult updateCheckerResult = updateChecker.checkUpdate();
            if (updateCheckerResult.isUpdateAvailable()) {
                Bukkit.getScheduler().runTask(core, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        
                        if (!hasSubPermissions(player, "checkupdate")) continue;
                        
                        player.sendMessage(Core.prefix + "§aAn update is available!");
                        player.sendMessage(Core.prefix + "§c" + updateCheckerResult.getOldVersion() + " §e-> §a" + updateCheckerResult.getNewVersion());
                    }
                });
            }
        });
    }
    
    private boolean hasSubPermissions(Player player, String perms) {
        return player.hasPermission(Variables.perms + "." + perms) || hasPermission(player);
    }
    
    private boolean hasPermission(Player player) {
        return player.hasPermission(Variables.perms + ".*") || player.isOp();
    }
}
