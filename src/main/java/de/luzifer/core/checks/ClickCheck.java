package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;

public class ClickCheck extends Check {

    @Override
    public void execute(User user) {

        if(user.getClicks() >= Variables.allowedClicks) {
            user.addViolation(ViolationType.EASY);
            if(user.getClicks() >= (Variables.allowedClicks + (Variables.allowedClicks/100)*10)) {
                user.addViolation(ViolationType.NORMAL);
            } else if(user.getClicks() >= (Variables.allowedClicks + (Variables.allowedClicks/100)*25)){
                user.addViolation(ViolationType.HARD);
            }

            if(!(Variables.sanctionateAtViolations > 0)) {
                user.sanction(true, User.CheckType.CLICK);
            }

        }

    }
}
