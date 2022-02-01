package de.luzifer.core.model.punishment;

import de.luzifer.core.model.user.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Punisher {
    
    public void applyPunishment(User user, Punishment punishment) {
        punishment.punish(user);
    }
    
}
