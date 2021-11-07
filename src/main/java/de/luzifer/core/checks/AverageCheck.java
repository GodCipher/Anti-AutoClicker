package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;

import java.util.List;

public class AverageCheck extends Check {
    
    @Override
    public void onSuccess(User user) {
        
        if(Variables.sanctionateAtViolations > 0) {
            user.addViolation(ViolationType.HARD);
            return;
        }
        
        user.sanction(false);
    }
    
    @Override
    public void onFailure(User user) {
    
    }
    
    @Override
    public boolean check(User user) {
        
        if(user.getClicks() >= Variables.averageCheckAtNeededClicks) {
            
            List<Double> clicksAverageCheckList = user.getClicksAverageCheckList();
            if(clicksAverageCheckList.size() >= Variables.averageCheckAtEntries) {
                
                double d = clicksAverageCheckList.get(0);
                for(double db : clicksAverageCheckList)
                    if(d != db)
                        return false;
            } else {
                
                return false;
            }
            
            return true;
        }
        
        return false;
    }
}
