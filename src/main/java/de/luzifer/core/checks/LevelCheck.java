package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;

import java.util.HashMap;

public class LevelCheck extends Check {
    
    private final HashMap<User, Integer> lastClick = new HashMap<>();
    
    @Override
    public void onSuccess(User user) {
        
        if (Variables.sanctionateAtViolations > 0) {
            user.addViolation(ViolationType.NORMAL);
            return;
        }
        
        user.sanction(false);
    }
    
    @Override
    public void onFailure(User user) {
    
    }
    
    @Override
    public boolean check(User user) {
        
        if (!lastClick.containsKey(user)) lastClick.put(user, user.getClicks());
        
        lastClick.put(user, user.getClicks());
        
        return user.getClicks() - lastClick.get(user) >= Variables.levelCheckMax;
    }
    
}
