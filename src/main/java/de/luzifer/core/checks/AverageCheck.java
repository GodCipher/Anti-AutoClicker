package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;

public class AverageCheck extends Check {

    @Override
    public void execute(User user) {
        if(user.getClicks() >= Variables.averageCheckAtNeededClicks) {
            double d;
            boolean isSame = true;
            if(user.getClicksAverageCheckList().size() >= Variables.averageCheckAtEntries) {
                d = user.getClicksAverageCheckList().get(0);
                for(double db : user.getClicksAverageCheckList()) {
                    if(d != db) {
                        isSame = false;
                        break;
                    }
                }
            } else {
                isSame = false;
            }

            if(isSame) {
                user.addViolation(ViolationType.HARD);

                if(!(Variables.sanctionateAtViolations > 0)) {
                    user.sanction(false, User.CheckType.AVERAGE);
                }
            }
        }
    }
}
