package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;

import java.util.HashMap;

public class LevelCheck extends Check {

    private final HashMap<User, Integer> lastClick = new HashMap<>();

    @Override
    public void execute(User user) {

        if(!lastClick.containsKey(user)) {
            lastClick.put(user, user.getClicks());
        }

        if(user.getClicks()-lastClick.get(user) >= Variables.levelCheckMax) {
            user.addViolation(ViolationType.NORMAL);

            if(!(Variables.sanctionateAtViolations > 0)) {
                user.sanction(false, User.CheckType.LEVEL);
            }
        }

        lastClick.put(user, user.getClicks());
    }

}
