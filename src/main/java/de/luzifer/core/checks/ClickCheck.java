package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;
import org.bukkit.configuration.file.FileConfiguration;

public class ClickCheck extends Check {
    
    @Override
    public void onSuccess(User user) {
        
        if (Variables.sanctionateAtViolations > 0) {
            user.addViolation(ViolationType.NORMAL);
            return;
        }
        
        user.sanction(true);
    }
    
    @Override
    public void onFailure(User user) {
    
    }
    
    @Override
    public boolean check(User user) {
        return user.getClicks() >= Variables.allowedClicks;
    }
}
